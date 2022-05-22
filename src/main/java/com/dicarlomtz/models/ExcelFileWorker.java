package com.dicarlomtz.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.util.HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;
import java.io.OutputStream;

public class ExcelFileWorker extends FileWorker {

    public ExcelFileWorker(Set<String> extensionsAllowed, String contextPath) throws IOException {
        super(extensionsAllowed);
        createFolders(contextPath);
        addDefaultExtentionsAllowed();
    }

    public ExcelFileWorker(String contextPath) throws IOException {
        this(new HashSet<String>(), contextPath);
    }

    public void createFolders(String contextPath) throws IOException {
        File processedFolder = new File(contextPath + "\\processed");
        File notApplicableFolder = new File(contextPath + "\\notApplicable");

        processedFolder.mkdir();
        notApplicableFolder.mkdir();

        try (Workbook workbook = new HSSFWorkbook()) {
            OutputStream fileOut = new FileOutputStream(contextPath + "\\processed\\" + "master.xls");
            workbook.createSheet("default");
            workbook.write(fileOut);
        }

    }

    public void addDefaultExtentionsAllowed() {
        getExtensionsAllowed().add("xls");

    }

    public void moveTo(String filePath, String folderPath) {
        File file = new File(filePath);
        File folder = new File(folderPath);

        file.renameTo(new File(folder.getAbsolutePath() + "\\" + file.getName()));
    }

    public void addToMasterFile(String filePath, String contextPath) {
        try {
            InputStream master = new FileInputStream(contextPath + "\\processed\\" + "master.xls");
            InputStream newFile = new FileInputStream(filePath);

            try {
                Workbook masterWb = WorkbookFactory.create(master);
                Workbook newWb = WorkbookFactory.create(newFile);

                int sheetsNumber = newWb.getNumberOfSheets();

                for (int i = 0; i < sheetsNumber; i++) {
                    Sheet sheet = newWb.getSheetAt(i);
                    Sheet masterSheet = masterWb.createSheet(sheet.getSheetName());
                    copySheets(masterSheet, sheet);
                }
                master.close();
                newFile.close();

                OutputStream output = new FileOutputStream(contextPath + "\\processed\\" + "master.xls");
                newWb.close();
                masterWb.write(output);
                masterWb.close();
                output.close();
            } catch (Exception e) {
                Logger.getLogger(ExcelFileWorker.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace();
            }

        } catch (IOException e1) {
            Logger.getLogger(ExcelFileWorker.class.getName()).log(Level.SEVERE, null, e1);
            e1.printStackTrace();
        }

    }

    public void copySheets(Sheet masterSheet, Sheet newSheet) {
        int rowsNumber = newSheet.getLastRowNum();
        int columnsNumber = newSheet.getRow(0).getLastCellNum();

        System.out.println(rowsNumber);
        System.out.println(columnsNumber);

        Iterator<Row> rowIterator = newSheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if (!Objects.isNull(row)) {
                Row newRow = masterSheet.createRow(row.getRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (!Objects.isNull(cell)) {
                        Cell newCell = newRow.createCell(cell.getColumnIndex());
                        newCell.setCellValue(cell.getStringCellValue());
                    }
                }
            }
        }

    }

    @Override
    void fileTaskWorker(String fileName, String contextPath) throws IOException {
        String filePath = contextPath + "\\" + fileName;

        if (isExtensionAllowed(fileName)) {
            moveTo(filePath, contextPath + "\\processed");
            addToMasterFile(contextPath + "\\processed\\" + fileName, contextPath);
        } else {
            moveTo(filePath, contextPath + "\\notApplicable");
        }
    }

}
