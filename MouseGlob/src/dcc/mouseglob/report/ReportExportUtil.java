package dcc.mouseglob.report;

import dcc.graphics.plot.oned.SeriesPlot;
import dcc.graphics.series.Series;
import dcc.graphics.math.Vector;
import dcc.mouseglob.FileType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Helpers to export report data to CSV and Parquet.
 */
public final class ReportExportUtil {
    private ReportExportUtil() {}

    public static void exportSeriesToCSV(SeriesReport report, Path file) throws IOException {
        if (!FileType.REPORT_CSV.validateExtension(file.toString())) {
            file = file.resolveSibling(FileType.REPORT_CSV.appendExtension(file.toString()));
        }
        SeriesPlot plot = report.getPlot();
        List<Series> seriesList = new ArrayList<>(plot.getSeries());
        try (Writer w = new OutputStreamWriter(Files.newOutputStream(file));
             CSVPrinter csv = new CSVPrinter(w, CSVFormat.DEFAULT)) {
            // Header: series_i_x, series_i_y
            List<String> header = new ArrayList<>();
            int nSeries = seriesList.size();
            for (int i = 0; i < nSeries; i++) {
                header.add("s" + i + "_x");
                header.add("s" + i + "_y");
            }
            csv.printRecord(header);

            // Determine max length among series
            int maxLen = 0;
            for (Series s : seriesList) maxLen = Math.max(maxLen, s.size());

            for (int row = 0; row < maxLen; row++) {
                List<Object> rec = new ArrayList<>();
                for (Series s : seriesList) {
                    Vector p = (row < s.size()) ? s.getPoint(row) : null;
                    if (p != null) { rec.add(p.x); rec.add(p.y); }
                    else { rec.add(""); rec.add(""); }
                }
                csv.printRecord(rec);
            }
        }
    }

}
