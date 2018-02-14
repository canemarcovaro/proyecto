/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Conexion;
import Modelo.Pagos;
import Vista.VistaPagos;
import Modelo.Cuenta;
import Modelo.EntidadCuentaCorriente;
import Modelo.MiModelo;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author JorgeA
 */

public class ControladorPago implements ActionListener, KeyListener, MouseListener {

    private Pagos p;
    private VistaPagos pp;
    private Cuenta cc;
    private EntidadCuentaCorriente ec;
    MiModelo modelo = new MiModelo();
    MiModelo c = new MiModelo();
    private boolean band = false;
    private boolean band2 = false;
    TableRowSorter trs;

    public ControladorPago(Pagos p, VistaPagos pp, Cuenta cc, EntidadCuentaCorriente ec, MiModelo mm) {

        this.p = p;
        this.pp = pp;
        this.pp.btnBuscar.addActionListener(this);
        this.pp.btnPago.addActionListener(this);
        this.pp.btnRPago.addActionListener(this);
        this.pp.btnCancelar.addActionListener(this);
        this.pp.btnCancelar2.addActionListener(this);
        this.pp.comboTar.addActionListener(this);
        this.pp.btnACuenta.addActionListener(this);
        //this.pp.tablaClientes.addMouseListener(this);
        this.pp.txtCuenta.addKeyListener(this);
        this.pp.btnAceptar.addActionListener(this);
        this.pp.btnEliminar.addActionListener(this);
        this.pp.tablaPagos.addMouseListener(this);
        this.pp.tablaClientes.addMouseListener(this);
        
    }
    

    public void iniciar() {

        MiModelo nuevo = new MiModelo();
        pp.setTitle("Pagos");
        pp.setLocationRelativeTo(null);
        pp.setVisible(true);
        pp.panelPago.setVisible(false);
        pp.btnPago.setEnabled(false);
        pp.txtSaldo.setText(null);
        pp.txtCuenta.setText(null);
        pp.panelInfo.setVisible(false);
        pp.btnACuenta.setEnabled(false);
        pp.txtCC.setVisible(false);
        pp.txtDni2.setVisible(false);
        pp.txtDni2.setText("");
        pp.venInterna.setVisible(false);
        pp.comboTar.setSelectedIndex(0);
        pp.btnEliminar.setEnabled(false);
        pp.txtDetalle.setEditable(false);
        pp.txtDetalle.setText("");
        
        
        c = new MiModelo();

        limpiarTablaPagos();
        cargarTablaPagos(nuevo);
        limpiarTablaClientes();
        try {
            cargarTablaClientes(c);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

        System.out.println(pp.txtDni2.getText());

        
    }
    
    public double filtrarTarifa() {

        int i = 0;
        String str = pp.comboTar.getSelectedItem().toString();
        String[] str1 = str.split("\\$");
        String str2 = str1[1];
        double num = 0.00;
        num = Double.parseDouble(str2);

        System.out.println(num);

        return num;
    }

    public void limpiarTablaPagos() {

        int sizeModel = pp.tablaPagos.getRowCount();

        for (int i = 0; i < sizeModel; i++) {
            pp.tablaPagos.removeRowSelectionInterval(0, sizeModel - 1);
        }

    }
    
      public void limpiarTablaClientes() {

        int sizeModel2 = pp.tablaClientes.getRowCount();

        for (int i = 0; i < sizeModel2; i++) {

            pp.tablaClientes.removeRowSelectionInterval(0, sizeModel2 - 1);
        }

    }

    public void cargarTablaPagos(MiModelo table) {

        try {

            PreparedStatement ps = null;
            pp.tablaPagos.setModel(table);

            String dni = pp.txtDni2.getText();

            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = conn.getConect();

            String sql = "SELECT b.id, b.fecha, b.debe, b.haber FROM clientes a, cuentacorriente b WHERE a.dni = '" + dni + "' and a.dni = b.dniCliente";
            String sql2 = "SELECT dni, fechaVto, baja FROM clientes WHERE dni = '" + dni + "'";

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
            table.addColumn("ID");
            table.addColumn("FECHA PAGO");
            table.addColumn("DEBE");
            table.addColumn("HABER");

            double debe = 0.00;
            double haber = 0.00;

            try {
                while (rs.next()) {

                    debe = debe + rs.getDouble(3);
                    haber = haber + rs.getDouble(4);

                    Object[] filas = new Object[cantCol];
                    for (int i = 0; i < cantCol; i++) {
                        filas[i] = rs.getObject(i + 1);

                    }

                    table.addRow(filas);

                }
               
                pp.txtSaldo.setText(String.format("%.2f",(haber - debe)));
               
                

            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                ps = con.prepareStatement(sql2);
            } catch (SQLException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }
            rs = ps.executeQuery();

            while (rs.next()) {

                if (rs.getInt(3) == 0) {

                    pp.txtECuenta.setText("ACTIVA");

                } else {

                    pp.txtECuenta.setText("VENCIDA");

                }

                pp.txtPVto.setText(String.valueOf(rs.getDate(2)));
                pp.txtDni2.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarTablaPagos2(MiModelo table, String dni) {

        try {

            PreparedStatement ps = null;
            pp.tablaPagos.setModel(table);

            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = conn.getConect();

            String sql = "SELECT b.id, b.fecha, b.debe, b.haber FROM clientes a, cuentacorriente b WHERE a.dni = '" + dni + "' and a.dni = b.dniCliente";
            String sql2 = "SELECT dni, fechaVto, baja FROM clientes WHERE dni = '" + dni + "'";
            

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
            
            table.addColumn("ID");
            table.addColumn("FECHA PAGO");
            table.addColumn("DEBE");
            table.addColumn("HABER");

            double debe = 0.00;
            double haber = 0.00;

            try {
                while (rs.next()) {

                    debe = debe + rs.getDouble(3);
                    haber = haber + rs.getDouble(4);

                    Object[] filas = new Object[cantCol];
                    for (int i = 0; i < cantCol; i++) {
                        filas[i] = rs.getObject(i + 1);

                    }

                    table.addRow(filas);

                }
                
                pp.txtSaldo.setText(String.format("%.2f",(haber - debe)));

            } catch (SQLException ex) {
                Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                ps = con.prepareStatement(sql2);
            } catch (SQLException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }
            rs = ps.executeQuery();

            while (rs.next()) {

                if (rs.getInt(3) == 0) {

                    pp.txtECuenta.setText("ACTIVA");

                } else {

                    pp.txtECuenta.setText("VENCIDA");

                }

                pp.txtPVto.setText(String.valueOf(rs.getDate(2)));
                pp.txtDni2.setText(String.valueOf(rs.getInt(1)));

            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevoPago() {

        try {
            PreparedStatement ps = null;

            String dni = pp.txtDni2.getText();

            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = conn.getConect();

            String sql = "SELECT dni, nombre FROM clientes WHERE dni = '" + dni + "'";
            String sql2 = "SELECT id, precio, nombre FROM tarifas";

            try {
                ps = con.prepareStatement(sql);
            } catch (SQLException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs = ps.executeQuery();

            } catch (SQLException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                while (rs.next()) {

                    Calendar fecha = Calendar.getInstance();

                    pp.txtDni.setText(String.valueOf(rs.getObject(1)));
                    pp.txtNombre.setText(String.valueOf(rs.getObject(2)));
                    pp.txtFecha.setText(String.valueOf(new java.sql.Date(fecha.getTimeInMillis())));

                }
            } catch (SQLException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (band2 == false) {
                try {
                    ps = con.prepareStatement(sql2);
                } catch (SQLException ex) {
                    Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
                }
                rs = ps.executeQuery();

                while (rs.next()) {

                    pp.comboTar.addItem(rs.getString(1) + " - " +rs.getString(3) + " :$"+rs.getString(2));

                }

                band2 = true;
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void realizarPago() {

        PreparedStatement ps = null;

        String dni = pp.txtDni2.getText();

        ResultSet rs = null;
        Conexion conn = new Conexion();
        Connection con = null;
        try {
            con = conn.getConect();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sql = "INSERT INTO cuentacorriente (id,dniCliente,fecha,debe,haber,detalle) VALUES (NULL,?,?,?,?,?)";

        try {
            ps = con.prepareStatement(sql);
            
            String detalle = String.valueOf(JOptionPane.showInputDialog(pp, "Detalle del Pago", null, JOptionPane.INFORMATION_MESSAGE));
            
            ps.setInt(1, Integer.parseInt(pp.txtDni.getText()));
            ps.setString(2, String.valueOf(pp.txtFecha.getText()));
            ps.setDouble(3, Double.parseDouble(pp.txtImporte.getText()) * Double.parseDouble(String.valueOf(pp.comboMeses.getSelectedItem())));
            ps.setDouble(4, Double.parseDouble(pp.txtMonto.getText()) * Double.parseDouble(String.valueOf(pp.comboMeses.getSelectedItem())));
            ps.setString(5, detalle);

            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void activarCuenta() {
        
        String[] meses = {"1","2","3","6","12"};
        PreparedStatement ps = null;

        String dni = pp.txtDni2.getText();

        ResultSet rs = null;
        Conexion conn = new Conexion();
        Connection con = null;
        try {
            con = conn.getConect();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sql = "UPDATE clientes SET fechaVal=?, fechaVto=?, baja=? WHERE dni=?";

        try {
            
            String valor = String.valueOf(JOptionPane.showInputDialog(pp,null,"Cantidad de Meses",JOptionPane.INFORMATION_MESSAGE,null,meses,meses[0]));
            System.out.println(valor);
            Calendar fechaVal = Calendar.getInstance();
            Calendar fechaVto = Calendar.getInstance();
            fechaVto.add(Calendar.DATE, Integer.parseInt(valor)*30);

            ps = con.prepareStatement(sql);

            ps.setInt(4, Integer.parseInt(pp.txtDni2.getText()));
            ps.setDate(1, new java.sql.Date(fechaVal.getTimeInMillis()));
            ps.setDate(2, new java.sql.Date(fechaVto.getTimeInMillis()));
            ps.setInt(3, 0);

            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cargarTablaClientes(MiModelo table) throws SQLException {

        try {
            
            trs = new TableRowSorter(c);
            pp.tablaClientes.setRowSorter(trs);
            PreparedStatement ps = null;

            pp.tablaClientes.setModel(table);
            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = conn.getConect();
            String sql = "SELECT dni,nombre,fechaVto,baja FROM clientes";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int cantCol = rsmd.getColumnCount();
            table.addColumn("DNI");
            table.addColumn("NOMBRE");
            table.addColumn("FECHA VENCIMIENTO");
            table.addColumn("ESTADO CUENTA");

            int j = 0;

            while (rs.next()) {
                Object[] filas = new Object[cantCol];
                for (int i = 0; i < cantCol; i++) {
                    filas[i] = rs.getObject(i + 1);

                }
                table.addRow(filas);
                if (rs.getInt(4) == 0) {
                    table.setValueAt("ACTIVA", j, 3);
                    j++;
                } else {
                    table.setValueAt("VENCIDA", j, 3);
                    j++;
                }
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
        public boolean eliminar(MiModelo table) throws SQLException{
        
        try {
            
            int filaEditar = pp.tablaPagos.getSelectedRow();
            
            int id = Integer.parseInt(String.valueOf(pp.tablaPagos.getValueAt(filaEditar, 0)));
            
            
            PreparedStatement ps = null;

            
            Conexion conn = new Conexion();
            Connection con = null;
            con = conn.getConect();
            
            String sql = "DELETE FROM cuentacorriente WHERE id = '"+ id +"'";
            
            System.out.println(sql);
            
             ps = con.prepareStatement(sql);
               
             ps.execute();
            
           
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return false;
        
           
        }

public void llenarDetalle() throws SQLException {

        try {

            PreparedStatement ps = null;
            
            int filaEditar = pp.tablaPagos.getSelectedRow();
            
            int id = Integer.parseInt(String.valueOf(pp.tablaPagos.getValueAt(filaEditar, 0)));

            
            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = conn.getConect();
            String sql = "SELECT detalle FROM cuentacorriente WHERE id = '" + id + "'";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while(rs.next()){
            pp.txtDetalle.setText(rs.getString(1));
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {

       
        if (e.getSource() == pp.btnBuscar) {
            
            pp.venInterna.setVisible(true);
            
            pp.txtCuenta.setText("");
            pp.txtCuenta.requestFocus();
            
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);

        }
       
        if (e.getSource() == pp.btnPago) {

            pp.panelPago.setVisible(true);
            pp.txtImporte.setText("0");
            pp.txtMonto.setText("0");
            pp.btnPago.setEnabled(false);
            nuevoPago();

        }
        if (e.getSource() == pp.btnRPago) {

            MiModelo b = new MiModelo();

            String botones[] = {"Si", "No"};
            int eleccion = JOptionPane.showOptionDialog(pp, "¿Desea realizar el siguiente pago?", "Confirmación de Pago", 0, 2, null, botones, this);
            if (eleccion == JOptionPane.YES_OPTION) {

                realizarPago();
                pp.btnPago.setEnabled(true);
                pp.panelPago.setVisible(false);
                pp.txtDni.setText(null);
                pp.txtNombre.setText(null);
                pp.txtFecha.setText(null);
                pp.txtImporte.setText(null);
                pp.txtMonto.setText(null);

                cargarTablaPagos(b);

            } else if (eleccion == JOptionPane.NO_OPTION) {

            }

        }
        if (e.getSource() == pp.btnCancelar) {

            pp.btnPago.setEnabled(true);
            pp.panelPago.setVisible(false);
            pp.txtDni.setText(null);
            pp.txtNombre.setText(null);
            pp.txtFecha.setText(null);
            pp.txtImporte.setText(null);
            pp.btnCancelar.setSelected(false);
            pp.txtMonto.setText(null);

        }
        if (e.getSource() == pp.btnCancelar2){
            
            
            pp.venInterna.setVisible(false);
            
            
        }
        if (e.getSource() == pp.comboTar) {

            if (pp.comboTar.getSelectedItem().toString().equals("Otros")) {

                pp.txtImporte.setEditable(true);
                pp.txtImporte.setText("0");
            } else {
                pp.txtImporte.setEditable(false);
                pp.txtImporte.setText(String.valueOf(filtrarTarifa()));

            }

        }
        if (e.getSource() == pp.btnACuenta) {

            MiModelo a = new MiModelo();

            String botones[] = {"Si", "No"};
            int eleccion = JOptionPane.showOptionDialog(pp, "¿Desea activar la cuenta " + pp.txtDni2.getText() + "?", "Renovando Cuenta", 0, 2, null, botones, this);
            if (eleccion == JOptionPane.YES_OPTION) {

                activarCuenta();
                limpiarTablaPagos();
                pp.btnACuenta.setEnabled(false);
                cargarTablaPagos(a);

            } else if (eleccion == JOptionPane.NO_OPTION) {

            }

        }
        if (e.getSource() == pp.btnAceptar) {

            int filaEditar = pp.tablaClientes.getSelectedRow();
            String dni = String.valueOf(pp.tablaClientes.getValueAt(filaEditar, 0));

            MiModelo a = new MiModelo();
            limpiarTablaPagos();
            cargarTablaPagos2(a, dni);
            pp.btnPago.setEnabled(true);
            pp.panelInfo.setVisible(true);
            pp.txtCC.setVisible(true);
            pp.txtDni2.setVisible(true);

            if (pp.txtECuenta.getText().equals("VENCIDA")) {

                pp.btnACuenta.setEnabled(true);

            } else {

                pp.btnACuenta.setEnabled(false);

            }

            pp.venInterna.setVisible(false);

        }
        if (e.getSource() == pp.btnEliminar) {
            
            MiModelo z = new MiModelo();  
            MiModelo y = new MiModelo();  
            
            String botones[] = {"Si", "No"};
            int eleccion = JOptionPane.showOptionDialog(pp, "¿Desea eliminar este pago?", "Eliminar Pago", 0, 2, null, botones, this);
            if (eleccion == JOptionPane.YES_OPTION) {

            try {
                
              eliminar(z);
              limpiarTablaPagos();
              cargarTablaPagos(y);
              pp.btnEliminar.setEnabled(false);
              
            } catch (SQLException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }

            } else if (eleccion == JOptionPane.NO_OPTION) {

            }
           
            
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == pp.txtCuenta) {
            
            trs = new TableRowSorter(c);
            pp.tablaClientes.setRowSorter(trs);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == pp.txtCuenta) {
            
            if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE){
           
               pp.txtCuenta.setText("");
           }
            
           if(e.getKeyChar() == KeyEvent.VK_0 || 
              e.getKeyChar() == KeyEvent.VK_1 ||
              e.getKeyChar() == KeyEvent.VK_2 ||
              e.getKeyChar() == KeyEvent.VK_3 ||
              e.getKeyChar() == KeyEvent.VK_4 || 
              e.getKeyChar() == KeyEvent.VK_5 ||
              e.getKeyChar() == KeyEvent.VK_6 ||
              e.getKeyChar() == KeyEvent.VK_7 ||
              e.getKeyChar() == KeyEvent.VK_8 || 
              e.getKeyChar() == KeyEvent.VK_9){
               
           trs.setRowFilter(RowFilter.regexFilter(pp.txtCuenta.getText(), 0));
           
           }
             if(!(e.getKeyChar() == KeyEvent.VK_0 || 
              e.getKeyChar() == KeyEvent.VK_1 ||
              e.getKeyChar() == KeyEvent.VK_2 ||
              e.getKeyChar() == KeyEvent.VK_3 ||
              e.getKeyChar() == KeyEvent.VK_4 || 
              e.getKeyChar() == KeyEvent.VK_5 ||
              e.getKeyChar() == KeyEvent.VK_6 ||
              e.getKeyChar() == KeyEvent.VK_7 ||
              e.getKeyChar() == KeyEvent.VK_8 || 
              e.getKeyChar() == KeyEvent.VK_9)){
               
           trs.setRowFilter(RowFilter.regexFilter("(?iu)"+pp.txtCuenta.getText(), 1));
          
           }
      
        }
           
    }

    

    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (e.getClickCount() == 1 && e.getSource() == pp.tablaPagos) {
            
                pp.btnEliminar.setEnabled(true);
            try {
                llenarDetalle();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorPago.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getClickCount() == 2 && e.getSource() == pp.tablaClientes) {
         
            int filaEditar = pp.tablaClientes.getSelectedRow();
            String dni = String.valueOf(pp.tablaClientes.getValueAt(filaEditar, 0));

            MiModelo a = new MiModelo();
            limpiarTablaPagos();
            cargarTablaPagos2(a, dni);
            pp.btnPago.setEnabled(true);
            pp.panelInfo.setVisible(true);
            pp.txtCC.setVisible(true);
            pp.txtDni2.setVisible(true);

            if (pp.txtECuenta.getText().equals("VENCIDA")) {

                pp.btnACuenta.setEnabled(true);

            } else {

                pp.btnACuenta.setEnabled(false);

            }

            pp.venInterna.setVisible(false);
        }
        

    }
    
    

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
  
    }

    @Override
    public void mouseReleased(MouseEvent e) {
 
    }

    @Override
    public void mouseEntered(MouseEvent e) {
  
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    
    
}