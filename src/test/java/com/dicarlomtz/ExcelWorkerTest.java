package com.dicarlomtz;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dicarlomtz.controllers.WatcherController;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Before;
import org.junit.Test;

public class ExcelWorkerTest {

    private WatcherController controller = WatcherController.getController();

    private final Path contextPath = Paths.get(System.getProperty("user.dir") + "\\test");
    private final Path processedPath = Paths.get(contextPath.toString() + "\\processed");
    private final Path notApplicablePath = Paths.get(contextPath.toString() + "\\notApplicable");
    private final Path masterFilePath = Paths.get(contextPath.toString() + "\\processed\\master.xls");

    @Before
    public void init() {
        try {
            controller.createExcelFolderWatcher(contextPath.toString());
        } catch (IOException | InterruptedException e) {
            Logger.getLogger(ExcelWorkerTest.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }

    @Test
    public void testerWrapper() {
        stage1createWorker();
        stage2createFolders();
        stage3createMasterXLS();
        stage4moveXLSFile();
        stage5verifyAddedXSLFile();
    }

    public void stage1createWorker() {
        assertEquals("Checking size of set", 1, controller.getSize());
    }

    public void stage2createFolders() {
        File fileNotProcessed = notApplicablePath.toFile().getAbsoluteFile();
        File fileProcessed = processedPath.toFile().getAbsoluteFile();

        boolean exist = fileNotProcessed.isDirectory() && fileProcessed.isDirectory();

        assertEquals("Checking if folders were created", true, exist);

    }

    public void stage3createMasterXLS() {
        File file = masterFilePath.toFile().getAbsoluteFile();

        boolean exist = file.exists();

        assertEquals("Checking if master.xls was created", true, exist);
    }

    public void stage4moveXLSFile() {
        Path creationPath = Paths.get(System.getProperty("user.dir") + "\\test.xls");
        Path initialPath = Paths.get(contextPath.toString() + "\\test.xls");
        Path finalPath = Paths.get(processedPath.toString() + "\\test.xls");

        try (Workbook workbook = new HSSFWorkbook()) {
            OutputStream fileOut = new FileOutputStream(creationPath.toFile());
            workbook.createSheet("test");
            Row row = workbook.getSheetAt(0).createRow(0);
            row.createCell(0).setCellValue("test");
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            Logger.getLogger(ExcelWorkerTest.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

        File createdFile = creationPath.toFile();
        createdFile.renameTo(new File(initialPath.toAbsolutePath().toString()));

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Logger.getLogger(ExcelWorkerTest.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

        File initialFile = initialPath.toFile().getAbsoluteFile();
        File movedFile = finalPath.toFile().getAbsoluteFile();

        boolean done = !initialFile.exists() && movedFile.exists();

        assertEquals("Checking if file was moved", true, done);
    }

    public void stage5verifyAddedXSLFile() {
        Sheet testSheet = null;

        try {
            InputStream masterFile = new FileInputStream(masterFilePath.toAbsolutePath().toString());
            Workbook masterWorkbook = WorkbookFactory.create(masterFile);
            testSheet = masterWorkbook.getSheet("test");
        } catch (EncryptedDocumentException | IOException e) {
            Logger.getLogger(ExcelWorkerTest.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

        assertEquals("Checking added sheet", false, Objects.isNull(testSheet));
    }

}