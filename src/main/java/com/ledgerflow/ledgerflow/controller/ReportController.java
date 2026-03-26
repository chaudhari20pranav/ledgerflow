package com.ledgerflow.ledgerflow.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ledgerflow.ledgerflow.dto.FinancialMetricsDto;
import com.ledgerflow.ledgerflow.model.User;
import com.ledgerflow.ledgerflow.service.FinancialService;
import com.ledgerflow.ledgerflow.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/report")
public class ReportController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FinancialService financialService;
    
    @GetMapping("/download")
    public void downloadReport(@RequestParam String type,
                               @RequestParam String format,
                               @RequestParam(required = false) Integer month,
                               @RequestParam(required = false) Integer year,
                               Authentication authentication,
                               HttpServletResponse response) throws Exception {
        
        User user = userService.findByUsername(authentication.getName());
        LocalDate startDate;
        LocalDate endDate;
        
        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
        } else {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }
        
        FinancialMetricsDto metrics = financialService.calculateMetrics(user, startDate, endDate);
        
        if ("pdf".equalsIgnoreCase(format)) {
            generatePdfReport(metrics, user, startDate, endDate, response);
        } else if ("excel".equalsIgnoreCase(format)) {
            generateExcelReport(metrics, user, startDate, endDate, response);
        }
    }
    
    private void generatePdfReport(FinancialMetricsDto metrics, User user, 
                                   LocalDate startDate, LocalDate endDate,
                                   HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", 
            "attachment; filename=financial_report_" + startDate + "_to_" + endDate + ".pdf");
        
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        
        // Title
        com.itextpdf.text.Font titleFont = 
        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
        Paragraph title = new Paragraph("LedgerFlow Financial Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        // Date Range
        com.itextpdf.text.Font dateFont =
        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);
        Paragraph dateRange = new Paragraph(
            "Report Period: " + startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + 
            " to " + endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), 
            dateFont);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        
        document.add(Chunk.NEWLINE);
        
        // Summary Table
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        
        addTableRow(summaryTable, "User:", user.getFullName());
        addTableRow(summaryTable, "Currency:", metrics.getCurrency());
        addTableRow(summaryTable, "Total Income:", 
            metrics.getCurrency() + " " + metrics.getTotalIncome());
        addTableRow(summaryTable, "Total Expenses:", 
            metrics.getCurrency() + " " + metrics.getTotalExpenses());
        addTableRow(summaryTable, "Net Profit:", 
            metrics.getCurrency() + " " + metrics.getNetProfit());
        addTableRow(summaryTable, "Savings Rate:", 
            String.format("%.2f", metrics.getSavingsRate()) + "%");
        
        document.add(summaryTable);
        document.add(Chunk.NEWLINE);
        
        // Expense Breakdown
        Paragraph expensesTitle = new Paragraph("Expense Breakdown", 
            new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD));
        document.add(expensesTitle);
        
        PdfPTable expenseTable = new PdfPTable(2);
        expenseTable.setWidthPercentage(100);
        expenseTable.addCell(createHeaderCell("Category"));
        expenseTable.addCell(createHeaderCell("Amount"));
        
        if (metrics.getCategoryWiseExpenses() != null) {
            for (var entry : metrics.getCategoryWiseExpenses().entrySet()) {
                expenseTable.addCell(entry.getKey());
                expenseTable.addCell(metrics.getCurrency() + " " + entry.getValue());
            }
        }
        
        document.add(expenseTable);
        document.add(Chunk.NEWLINE);
        
        // Income Breakdown
        Paragraph incomeTitle = new Paragraph("Income Breakdown",
    new com.itextpdf.text.Font(
        com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD));
        document.add(incomeTitle);
        
        PdfPTable incomeTable = new PdfPTable(2);
        incomeTable.setWidthPercentage(100);
        incomeTable.addCell(createHeaderCell("Category"));
        incomeTable.addCell(createHeaderCell("Amount"));
        
        if (metrics.getCategoryWiseIncome() != null) {
            for (var entry : metrics.getCategoryWiseIncome().entrySet()) {
                incomeTable.addCell(entry.getKey());
                incomeTable.addCell(metrics.getCurrency() + " " + entry.getValue());
            }
        }
        
        document.add(incomeTable);
        
        document.close();
    }
    
    private void generateExcelReport(FinancialMetricsDto metrics, User user,
                                     LocalDate startDate, LocalDate endDate,
                                     HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", 
            "attachment; filename=financial_report_" + startDate + "_to_" + endDate + ".xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial Report");
        
        // Header
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // Title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("LedgerFlow Financial Report");
        titleCell.setCellStyle(headerStyle);
        
        Row dateRow = sheet.createRow(1);
        dateRow.createCell(0).setCellValue("Period: " + startDate + " to " + endDate);
        
        int rowNum = 3;
        
        // Summary
        Row summaryHeader = sheet.createRow(rowNum++);
        summaryHeader.createCell(0).setCellValue("Summary");
        summaryHeader.getCell(0).setCellStyle(headerStyle);
        
        addExcelRow(sheet, rowNum++, "User:", user.getFullName());
        addExcelRow(sheet, rowNum++, "Currency:", metrics.getCurrency());
        addExcelRow(sheet, rowNum++, "Total Income:", metrics.getTotalIncome().toString());
        addExcelRow(sheet, rowNum++, "Total Expenses:", metrics.getTotalExpenses().toString());
        addExcelRow(sheet, rowNum++, "Net Profit:", metrics.getNetProfit().toString());
        addExcelRow(sheet, rowNum++, "Savings Rate:", String.format("%.2f", metrics.getSavingsRate()) + "%");
        
        rowNum++;
        
        // Expense Breakdown
        Row expenseHeader = sheet.createRow(rowNum++);
        expenseHeader.createCell(0).setCellValue("Expense Breakdown");
        expenseHeader.getCell(0).setCellStyle(headerStyle);
        
        Row expenseCols = sheet.createRow(rowNum++);
        expenseCols.createCell(0).setCellValue("Category");
        expenseCols.createCell(1).setCellValue("Amount");
        
        if (metrics.getCategoryWiseExpenses() != null) {
            for (var entry : metrics.getCategoryWiseExpenses().entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(metrics.getCurrency() + " " + entry.getValue());
            }
        }
        
        rowNum++;
        
        // Income Breakdown
        Row incomeHeader = sheet.createRow(rowNum++);
        incomeHeader.createCell(0).setCellValue("Income Breakdown");
        incomeHeader.getCell(0).setCellStyle(headerStyle);
        
        Row incomeCols = sheet.createRow(rowNum++);
        incomeCols.createCell(0).setCellValue("Category");
        incomeCols.createCell(1).setCellValue("Amount");
        
        if (metrics.getCategoryWiseIncome() != null) {
            for (var entry : metrics.getCategoryWiseIncome().entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(metrics.getCurrency() + " " + entry.getValue());
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < 2; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    private void addTableRow(PdfPTable table, String label, String value) {
        table.addCell(createCell(label, com.itextpdf.text.Font.BOLD));
        table.addCell(createCell(value, com.itextpdf.text.Font.NORMAL));
    }
    
    private PdfPCell createCell(String text, int style) {
        com.itextpdf.text.Font font =
        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, style);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }
    
    private PdfPCell createHeaderCell(String text) {
        com.itextpdf.text.Font font =
    new com.itextpdf.text.Font(
        com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }
    
    private void addExcelRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }
}