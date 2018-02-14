/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

/**
 *
 * @author JorgeA
 */
import Modelo.Conexion;
import Vista.VistaReportes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author JorgeA
 */
public class ControladorReportes implements ActionListener {

    private VistaReportes vr;

    public ControladorReportes(VistaReportes vr) {
        this.vr = vr;
        this.vr.reporte1.addActionListener(this);
        this.vr.reporte2.addActionListener(this); 
    }

    public void iniciar() {
        vr.setTitle("Reportes");
        vr.setLocationRelativeTo(null);
        vr.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vr.reporte1) {
            try {
                
                Conexion con = new Conexion();

                Connection conn = null;
                try {
                    conn = con.getConect();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ControladorReportes.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                JasperReport reporte = null;
                //String path = "C:\\Reportes\\report1.jasper";
                
                Date fecha1 = new java.sql.Date(vr.fecha1.getDate().getTime());

                Date fecha2 = new java.sql.Date(vr.fecha2.getDate().getTime());

                SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");

                String fecha11 = dt.format(fecha1);
                String fecha12 = dt.format(fecha2);
                
                Map parametro = new HashMap();
               
                parametro.put("fecha1", fecha11);
                parametro.put("fecha2", fecha12);
                
                reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/report1.jasper"));
                
                JasperPrint jprint = JasperFillManager.fillReport(reporte, parametro, conn);
                JasperViewer view = new JasperViewer(jprint, false);
                view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                
                view.setVisible(true);

            } catch (JRException ex) {
                Logger.getLogger(ControladorReportes.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if(e.getSource() == vr.reporte2){
              try {
                Conexion con = new Conexion();

                Connection conn = null;
                try {
                    conn = con.getConect();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ControladorReportes.class.getName()).log(Level.SEVERE, null, ex);
                }

                JasperReport reporte = null;
                //String path = System.getProperty("user.dir") + "\\src\\Reportes\\report2.jasper";

                
                reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/report2.jasper"));

                JasperPrint jprint = JasperFillManager.fillReport(reporte, null, conn);
                JasperViewer view = new JasperViewer(jprint, false);
                view.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                view.setVisible(true);

            } catch (JRException ex) {
                Logger.getLogger(ControladorReportes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            
    }

}
