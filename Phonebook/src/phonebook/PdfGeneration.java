package phonebook;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javafx.collections.ObservableList;

public class PdfGeneration {
    public void pdfGeneration (String fileName, ObservableList<Person> data) {
        Document document = new Document();
        
        try{
            //Logo
            PdfWriter.getInstance(document, new FileOutputStream(fileName + ".pdf"));
            document.open();
            Image image = Image.getInstance(getClass().getResource("/logo.png"));
            image.scaleToFit(500,186);
            image.setAbsolutePosition(60F, 750f);
            document.add(image);
            
            //Line break
            document.add(new Paragraph("\n\n\n\n\n\n"));
            
            //Table
            float[] columnWidht = {1, 3, 3, 4};
            PdfPTable table = new PdfPTable(columnWidht);
            table.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("KontaktLista"));
            cell.setBackgroundColor(GrayColor.GRAYWHITE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(4);
            table.addCell(cell);
            
            table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell("Sorszám");
            table.addCell("Vezetéknév");
            table.addCell("Keresztnév");
            table.addCell("E-mail cím");
            table.setHeaderRows(1);
            
            table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            
            for(int i = 1; i <= data.size(); i++) {
                Person actualPerson = data.get(i -1);
                table.addCell(String.valueOf(i));
                table.addCell(actualPerson.getLastName());
                table.addCell(actualPerson.getFirstName());
                table.addCell(actualPerson.getEmail());
            }
            
            document.add(table);
            
            //Signature
            Chunk signature = new Chunk("\n\n\n Generálva a Telefonkönyv alkalmazás segítségével!");
            Paragraph base = new Paragraph(signature);
            document.add(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.close();
    }
}
