package dcc.mouseglob.tracking.pipeline;

import dcc.mouseglob.tracking.pipeline.stages.AdaptiveThresholdStage;
import dcc.mouseglob.tracking.pipeline.stages.BackgroundSubtractStage;
import dcc.mouseglob.tracking.pipeline.stages.GrayscaleStage;
import dcc.mouseglob.tracking.pipeline.stages.MorphologyStage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Extremely small JSON loader tailored for pipeline configuration.
 * Supports a top-level object with an array field "stages" composed of objects with
 * simple string/number/boolean properties.
 */
public final class JsonPipelineLoader {
    private JsonPipelineLoader() {}

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JsonPipelineLoader.class);

    public static FramePipeline loadFromResource(String resourcePath) {
        if (resourcePath == null) return null;
        String rp = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        try (InputStream in = JsonPipelineLoader.class.getResourceAsStream(rp)) {
            if (in == null) {
                log.warn("Pipeline resource not found: {}", resourcePath);
                return null;
            }
            String json = readAll(in);
            return parse(json);
        } catch (IOException e) {
            log.warn("Failed to read pipeline resource {}: {}", resourcePath, e.toString());
            return null;
        }
    }

    public static FramePipeline loadFromFile(String filePath) {
        try {
            String json = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            return parse(json);
        } catch (IOException e) {
            log.warn("Failed to load pipeline file {}: {}", filePath, e.toString());
            return null;
        }
    }

    public static FramePipeline parse(String json) {
        if (json == null) return null;
        List<Map<String,Object>> stages = extractStages(json);
        if (stages.isEmpty()) return null;
        FramePipeline pipeline = new FramePipeline();
        for (Map<String,Object> def : stages) {
            String type = (String) def.get("type");
            if (type == null) continue;
            Stage stage = create(type);
            if (stage instanceof Stage.ConfigurableStage) {
                Map<String,Object> params = new LinkedHashMap<>(def);
                params.remove("type");
                ((Stage.ConfigurableStage) stage).configure(params);
            }
            pipeline.add(stage);
        }
        return pipeline;
    }

    private static Stage create(String type) {
        String t = type.toLowerCase();
        switch (t) {
            case "grayscale": return new GrayscaleStage();
            case "backgroundsubtract":
            case "background": return new BackgroundSubtractStage();
            case "adaptivethreshold":
            case "threshold": return new AdaptiveThresholdStage();
            case "morphology": return new MorphologyStage();
            default:
                log.warn("Unknown stage type: {}", type);
                return null;
        }
    }

    // ---- ultra-light JSON routines ----
    private static String readAll(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line; while ((line = br.readLine()) != null) sb.append(line).append('\n');
        return sb.toString();
    }

    private static List<Map<String,Object>> extractStages(String json) {
        List<Map<String,Object>> list = new ArrayList<>();
        int idx = json.indexOf("\"stages\"");
        if (idx < 0) return list;
        int start = json.indexOf('[', idx);
        int end = findMatching(json, start, '[', ']');
        if (start < 0 || end < 0) return list;
        String array = json.substring(start + 1, end);
        int pos = 0;
        while (true) {
            int objStart = array.indexOf('{', pos);
            if (objStart < 0) break;
            int objEnd = findMatching(array, objStart, '{', '}');
            if (objEnd < 0) break;
            String obj = array.substring(objStart + 1, objEnd);
            list.add(parseObject(obj));
            pos = objEnd + 1;
        }
        return list;
    }

    private static int findMatching(String s, int start, char open, char close) {
        int depth = 0; boolean inStr = false; char prev = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && prev != '\\') inStr = !inStr;
            if (inStr) { prev = c; continue; }
            if (c == open) depth++;
            else if (c == close) { depth--; if (depth == 0) return i; }
            prev = c;
        }
        return -1;
    }

    private static Map<String,Object> parseObject(String obj) {
        Map<String,Object> map = new LinkedHashMap<>();
        // split by commas not inside strings
        int pos = 0; boolean inStr = false; char prev = 0;
        List<String> pairs = new ArrayList<>();
        int last = 0;
        for (int i = 0; i < obj.length(); i++) {
            char c = obj.charAt(i);
            if (c == '"' && prev != '\\') inStr = !inStr;
            if (!inStr && c == ',') { pairs.add(obj.substring(last, i)); last = i+1; }
            prev = c;
        }
        if (last < obj.length()) pairs.add(obj.substring(last));
        for (String p : pairs) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2) continue;
            String key = strip(q(kv[0]));
            String val = strip(kv[1]);
            map.put(key, coerce(val));
        }
        return map;
    }

    private static String q(String s) {
        int i = s.indexOf('"');
        int j = s.lastIndexOf('"');
        if (i >= 0 && j > i) return s.substring(i+1, j);
        return s.trim();
    }

    private static String strip(String s) { return s.trim(); }

    private static Object coerce(String v) {
        v = v.trim();
        if (v.startsWith("\"")) return q(v);
        if ("true".equalsIgnoreCase(v)) return Boolean.TRUE;
        if ("false".equalsIgnoreCase(v)) return Boolean.FALSE;
        try { if (v.contains(".")) return Double.parseDouble(v); else return Integer.parseInt(v); } catch (Exception ignore) {}
        return v;
    }
}
