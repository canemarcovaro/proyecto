/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Conexion;
import Modelo.MiModelo;
import Vista.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author JorgeA
 */
public class ControladorMenu implements ActionListener {

    private ControladorClientes cc;
    private Menu m;
    private ControladorTarifas ct;
    MiModelo modelo = new MiModelo();
    private ControladorPago cp;
    private ControladorReportes cr;
    
    public ControladorMenu(ControladorClientes cc,ControladorReportes cr, Menu m, ControladorTarifas ct,ControladorPago cp, MiModelo mm) {
        this.cc = cc;
        this.cp = cp;
        this.m = m;
        this.cr = cr;
        this.m.btnClientes.addActionListener(this);
        this.m.btnTarifas.addActionListener(this);
        this.m.acTabla.addActionListener(this);
        this.ct = ct;
        this.m.btnPagos.addActionListener(this);
        this.m.btnRepo.addActionListener(this);
    }

    public void iniciar() {

        m.setTitle("Menu");
        m.setLocationRelativeTo(null);
        m.setVisible(true);
        verificarVto();
        cargarTablaVto(modelo);
    
    }
    
    public void limpiarTabla() {

        int sizeModel = m.tablaVto.getRowCount();

        for (int i = 0; i < sizeModel; i++) {
            m.tablaVto.removeRowSelectionInterval(0, sizeModel - 1);
        }

    }
    
    public void actualizarTabla() {

        MiModelo a = new MiModelo();
        limpiarTabla();
        verificarVto();
        cargarTablaVto(a);

    }

public void verificarVto() {
        
        Calendar fechaHoy = Calendar.getInstance();
       
        try {

            PreparedStatement ps = null;

            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = null;
            try {
                con = conn.getConect();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sql = "SELECT fechaVto,dni FROM clientes";
            try {
                ps = con.prepareStatement(sql);
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (rs.next()) {
                
                Calendar fechaVto2 = Calendar.getInstance();
                Calendar fechaVto = Calendar.getInstance();
                
                fechaVto.setTime(rs.getDate(1));
                fechaVto2.setTime(rs.getDate(1));
                
                fechaVto.add(Calendar.DATE, -5);
                
                    
                 if(fechaHoy.equals(fechaVto2) || fechaHoy.after(fechaVto2)){
                        
                    String sql2 = "UPDATE clientes SET baja = true WHERE dni=" + rs.getInt(2);
                    
                    
                    ps = con.prepareStatement(sql2);
                    ps.execute();
                    
                     
                    
                }
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

    public void cargarTablaVto(MiModelo table) {

        try {
            PreparedStatement ps = null;
            m.tablaVto.setModel(table);

            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = conn.getConect();

            String sql = "SELECT dni, nombre, fechaVto, baja FROM clientes WHERE baja = true ";

            try {
                ps = con.prepareStatement(sql);
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            ResultSetMetaData rsmd = null;
            try {
                rsmd = rs.getMetaData();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }

            int cantCol = 0;

            try {
                cantCol = rsmd.getColumnCount();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }

            table.addColumn("DNI");
            table.addColumn("NOMBRE");
            table.addColumn("FECHA VENCIMIENTO");
            table.addColumn("ESTADO");
            
            int j = 0;

            try {
                while (rs.next()) {


                    Object[] filas = new Object[cantCol];
                    
                    for (int i = 0; i < cantCol; i++) {
                    filas[i] = rs.getObject(i + 1);
                    
                       
                    }
                    
                    table.addRow(filas);
                    table.setValueAt("VENCIDO", j, 3);
                    j++;
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

     @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == m.btnClientes) {
            try {
                iniciarVistaMenuCliente();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getSource() == m.btnTarifas) {
            ct.iniciarTarifas();
        }
        if(e.getSource() == m.btnPagos){
            
            cp.iniciar();
        }
        if (e.getSource() == m.acTabla) {

            actualizarTabla();

        }
        if(e.getSource() == m.btnRepo){
            cr.iniciar();
            
        }
    }

    public void iniciarVistaMenuCliente() throws SQLException {
        cc.iniciarMenuCliente();

    }
}
