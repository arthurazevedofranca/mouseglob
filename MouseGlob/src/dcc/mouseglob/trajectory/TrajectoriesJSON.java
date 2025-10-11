package dcc.mouseglob.trajectory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dcc.mouseglob.MouseGlob;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.calibration.CalibrationModule;
import dcc.mouseglob.maze.Zone;
import dcc.mouseglob.maze.ZonesManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Minimal JSON export for trajectories as NDJSON following schema in resource/schemas/trajectory.schema.json.
 * This utility converts an existing parsed CSV trajectories file into NDJSON for interoperability.
 */
public final class TrajectoriesJSON {
    private TrajectoriesJSON() {}

    /**
     * Exports the parsed trajectories from a TrajectoryReader into NDJSON lines at the given path.
     * First line is a metadata object, followed by one frame per line.
     */
    public static void exportNdjson(TrajectoryReader reader, ZonesManager zonesManager, Path outFile) throws IOException {
        ObjectMapper om = new ObjectMapper();
        StringBuilder sb = new StringBuilder();

        // Metadata (best-effort)
        ObjectNode meta = om.createObjectNode();
        meta.put("type", "meta");
        meta.put("version", MouseGlob.VERSION);
        // Size is unknown here; store as -1
        meta.put("width", -1);
        meta.put("height", -1);
        Calibration calib = CalibrationModule.getInstance().getModel();
        meta.put("scale_cm_per_px", calib != null ? calib.getScale() : 0.0);
        ArrayNode zones = om.createArrayNode();
        if (zonesManager != null) {
            for (Zone z : zonesManager.getZones()) zones.add(z.getName());
        }
        meta.set("zones", zones);
        sb.append(meta.toString()).append('\n');

        // Frames
        List<Trajectory> trajectories = reader.getTrajectories();
        if (trajectories == null || trajectories.isEmpty()) {
            Files.writeString(outFile, sb.toString());
            return;
        }
        // Emit one frame per sample per trajectory (best-effort ordering)
        for (Trajectory t : trajectories) {
            int i = 0;
            for (dcc.graphics.math.Vector v : t) {
                ObjectNode frame = om.createObjectNode();
                frame.put("type", "frame");
                frame.put("t_ms", i++);
                ArrayNode tracks = om.createArrayNode();
                ObjectNode tr = om.createObjectNode();
                tr.put("name", t.getName());
                tr.put("x_cm", v.x);
                tr.put("y_cm", v.y);
                tracks.add(tr);
                frame.set("trackers", tracks);
                sb.append(frame.toString()).append('\n');
            }
        }
        Files.writeString(outFile, sb.toString());
    }
}
