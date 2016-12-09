package org.jdal.samples.vaadin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.jdal.beans.MessageSourceWrapper;
import org.jdal.dao.Dao;
import org.jdal.samples.model.User;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class AboutMainView extends TabSheet implements View, StreamSource {

    private MessageSourceWrapper messageSource = new MessageSourceWrapper();
    private VerticalLayout aboutPanel;

    @Resource
    private Dao<User, Long> userService;

    public static String rowData = "";

    public AboutMainView() {
    }

    @PostConstruct
    public void init() {
        setStyleName(Reindeer.TABSHEET_MINIMAL);
        addTabComponent("Generate Report", getAboutPanel());
    }

    private void addTabComponent(String caption, Component component) {
        addTab(component, messageSource.getMessage(caption));
    }

    private Component getAboutPanel() {
        if (aboutPanel == null) {
            this.aboutPanel = createVerticalLayout();
        }
        return this.aboutPanel;
    }

    /**
     * Create a new {@link VerticalLayout}
     */
    private VerticalLayout createVerticalLayout() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setMargin(true);
        List<User> users = userService.getAll();
        for (User user : users) {
            /* if (!user.getUsername().equals("admin")) { */
            rowData = rowData
                            + "<fo:table-row><fo:table-cell border='solid black 1px' padding='2px'><fo:block>"
                            + user.getName()
                            + "</fo:block> </fo:table-cell><fo:table-cell border='solid black 1px' padding='2px'><fo:block>"
                            + user.getSurname()
                            + "</fo:block> </fo:table-cell><fo:table-cell border='solid black 1px' padding='2px'><fo:block>"
                            + user.getEmail() + "</fo:block></fo:table-cell></fo:table-row>";
            System.out.println("------------" + user.getEmail());
            /* } */
        }
        // This actually opens the stream resource
        final Button print = new Button("Users Report");
        print.setEnabled(true);

        AboutMainView source = new AboutMainView();
        // Create the stream resource and give it a file name
        String filename = "pdf_printing_example.pdf";
        StreamResource resource = new StreamResource(source, filename);

        // These settings are not usually necessary. MIME type
        // is detected automatically from the file name, but
        // setting it explicitly may be necessary if the file
        // suffix is not ".pdf".
        resource.setMIMEType("application/pdf");
        resource.getStream().setParameter("Content-Disposition", "attachment; filename=" + filename);

        // Extend the print button with an opener
        // for the PDF resource
        BrowserWindowOpener opener = new BrowserWindowOpener(resource);
        opener.extend(print);
        print.setEnabled(true);

        // Re-enable editing after printing
        print.addClickListener(new ClickListener() {
            private static final long serialVersionUID = -5413419737626607326L;

            @Override
            public void buttonClick(ClickEvent event) {

            }
        });
        vl.addComponent(print);
        return vl;
    }

    @Override
    public InputStream getStream() {
        // Generate the FO content. You could use the Java DOM API
        // here as well and pass the DOM to the transformer.
        String fo = "<?xml version='1.0' encoding='ISO-8859-1'?><fo:root xmlns:fo='http://www.w3.org/1999/XSL/Format'><fo:layout-master-set> <fo:simple-page-master master-name='my-page' page-height='8.5in' page-width='11in'><fo:region-body margin='1in' margin-top='1.5in' margin-bottom='1.5in'/></fo:simple-page-master></fo:layout-master-set><fo:page-sequence master-reference='my-page'><fo:flow flow-name='xsl-region-body'><fo:block font-size='18pt' font-family='sans-serif' line-height='24pt' space-after.optimum='15pt' color='black' text-align='center' padding-top='3pt'>Users Report</fo:block>/n<fo:table><fo:table-header><fo:table-row><fo:table-cell width='5cm' border='solid black 1px' padding='2px' font-weight='bold' text-align='center'><fo:block>First Name</fo:block></fo:table-cell><fo:table-cell width='10cm' border='solid black 1px' padding='2px' font-weight='bold' text-align='center'><fo:block>Last Name</fo:block></fo:table-cell><fo:table-cell width='8cm' border='solid black 1px' padding='2px' font-weight='bold' text-align='center'><fo:block>Email Id</fo:block></fo:table-cell></fo:table-row></fo:table-header><fo:table-body>"
                        + rowData + "</fo:table-body></fo:table></fo:flow></fo:page-sequence></fo:root>";
        ByteArrayInputStream foStream = new ByteArrayInputStream(fo.getBytes());
        // Basic FOP configuration. You could create this object
        // just once and keep it.
        FopFactory fopFactory = FopFactory.newInstance();
        fopFactory.setStrictValidation(false); // For an example

        // Configuration for this PDF document - mainly metadata
        FOUserAgent userAgent = fopFactory.newFOUserAgent();
        userAgent.setProducer("My Vaadin Application");
        userAgent.setCreationDate(new Date());
        userAgent.setTitle("Users Report");
        userAgent.setKeywords("Users Report");
        userAgent.setTargetResolution(300); // DPI

        // Transform to PDF
        ByteArrayOutputStream fopOut = new ByteArrayOutputStream();
        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, fopOut);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            Source src = new javax.xml.transform.stream.StreamSource(foStream);
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
            fopOut.close();
            return new ByteArrayInputStream(fopOut.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        rowData = "";
        createVerticalLayout();
    }
}
