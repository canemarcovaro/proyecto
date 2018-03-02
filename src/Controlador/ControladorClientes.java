/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Cliente;
import Modelo.Conexion;
import Modelo.Cuenta;
import Modelo.EntidadCliente;
import Modelo.EntidadCuentaCorriente;
import Modelo.EntidadTarifa;
import Modelo.MiModelo;
import Vista.Clientes;
import Vista.MenuClientes;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author JorgeA
 */
public class ControladorClientes implements ActionListener, KeyListener, MouseListener {

    private String foto;
    private File file;
    private Clientes c;
    private Cliente cl;
    private EntidadCliente en;
    private MenuClientes mc;
    MiModelo modelo = new MiModelo();
    TableRowSorter trs;
    private int filaEditar;
    private int numFila;
    boolean band = false;
    private Cuenta cu;
    private EntidadTarifa et;
    private EntidadCuentaCorriente ec;

    public ControladorClientes(Clientes c, Cliente cl, EntidadCliente en, MenuClientes mc, Cuenta cu, EntidadTarifa et, EntidadCuentaCorriente ec, MiModelo mm) {
        this.c = c;
        this.cl = cl;
        this.cu = cu;
        this.en = en;
        this.mc = mc;
        this.et = et;
        this.ec = ec;
        this.c.btnGuardar.addActionListener(this);
        this.mc.btnNuevo.addActionListener(this);
        this.c.btnEdit.addActionListener(this);
        this.mc.txtBuscar.addKeyListener(this);
        this.mc.btnMod.addActionListener(this);
        this.mc.tabla1.addMouseListener(this);
        this.c.btnCancel.addActionListener(this);
        this.c.btnSubir.addActionListener(this);

    }

    public void iniciarClientes() {

        c.setTitle("Clientes");
        c.setLocationRelativeTo(null);
        c.setVisible(true);

    }

    public void iniciarMenuCliente() throws SQLException {

        mc.setTitle("Menu Clientes");
        mc.setLocationRelativeTo(null);
        mc.setVisible(true);

        //Fixed, se incorporó bandera para solucionar problema de Dispose con la Vista MenuClientes...
        if (band == false) {
            cargarTabla();
            band = true;
        }

    }

    public void calcularFecha() {

        Date fechaVal = c.fechaIngreso.getDate();

        cl.setFechaVal(new java.sql.Date(fechaVal.getTime()));
        cl.setFechaVto(new java.sql.Date(fechaVal.getTime()));

    }

    public void cargarTabla() throws SQLException {

        try {

            PreparedStatement ps = null;

            mc.tabla1.setModel(modelo);
            ResultSet rs = null;
            Conexion conn = new Conexion();
            Connection con = conn.getConect();
            String sql = "SELECT dni,nombre,fechaNac,nroTel,direccion,mail FROM clientes";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int cantCol = rsmd.getColumnCount();
            modelo.addColumn("DNI");
            modelo.addColumn("NOMBRE");
            modelo.addColumn("FECHA NACIMIENTO");
            modelo.addColumn("TELEFONO");
            modelo.addColumn("DOMICILIO");
            modelo.addColumn("MAIL");

            while (rs.next()) {
                Object[] filas = new Object[cantCol];
                for (int i = 0; i < cantCol; i++) {
                    filas[i] = rs.getObject(i + 1);

                }
                modelo.addRow(filas);

            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == mc.btnNuevo) {

            c.txtDni.setEditable(true);
            c.btnGuardar.setVisible(true);

            iniciarClientes();

            Date fechaVal = Calendar.getInstance().getTime();
            c.fechaIngreso.setDate(fechaVal);
            c.fechaIngreso2.setVisible(true);
            c.btnEdit.setVisible(false);
            c.txtDirec.setText(null);
            c.txtDni.setText(null);
            c.txtEmail.setText(null);
            c.txtNombre.setText(null);
            c.txtNum.setText(null);

            c.fecha.setDate(Calendar.getInstance().getTime());
            c.fechaIngreso.setDate(Calendar.getInstance().getTime());
            Image image1 = new ImageIcon(getClass().getResource("/Imagenes/perfil2.png")).getImage();
            image1.getScaledInstance(250, 300, Image.SCALE_DEFAULT);
            c.labFoto.setIcon(new ImageIcon(image1));

        }
        if (e.getSource() == c.btnGuardar) {

            c.txtDni.setEditable(true);

            if (c.txtDni.getText().length() == 0 || c.txtDirec.getText().length() == 0 || c.txtNombre.getText().length() == 0 || c.txtEmail.getText().length() == 0 || c.txtNum.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Error campos incompletos");

            }

            SimpleDateFormat formatter = new SimpleDateFormat("aa/MM/dddd");
            
            
            String dni = c.txtDni.getText().replaceAll(" ", "");
            String nroTel = c.txtNum.getText().replaceAll(" ","");
            
            System.out.println(dni);
            
            cl.setDni(Integer.parseInt(dni));
            cl.setNombre(c.txtNombre.getText());
            cl.setFechaNac(new java.sql.Date(c.fecha.getDate().getTime()));
            cl.setNroTel(Integer.parseInt(nroTel));
            cl.setDirec(c.txtDirec.getText());
            cl.setMail(c.txtEmail.getText());
            cl.setRuta(foto);
            calcularFecha();
            boolean b = false;

            try {
                String botones[] = {"Si", "No"};
                int eleccion = JOptionPane.showOptionDialog(c, "¿Desea crear el cliente: " + dni + "? Una vez creado no podrá modificar el DNI", "Creando Cliente", 0, 2, null, botones, this);
                if (eleccion == JOptionPane.YES_OPTION) {

                    if (c.txtNombre.getText().length() > 0) {
                        if (en.registrar(cl)) {

                            JOptionPane.showMessageDialog(null, "registro guardado");
                            modelo.addRow(new Object[]{cl.getDni(), cl.getNombre(), cl.getFechaNac(), cl.getNroTel(), cl.getDirec(), cl.getMail()});
                            b = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "ERROR: DNI existente ");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "ERROR: Campo 'nombre' incompleto ");

                    }

                } else if (eleccion == JOptionPane.NO_OPTION) {

                }

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (b == true) {
                c.dispose();
                b = false;
            }

        }
        if (e.getSource() == c.btnSubir) {
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Formato de Archivos: JPEG(.JPG;.JPEG)", "jpg", "jpeg");
            JFileChooser archivo = new JFileChooser();
            archivo.addChoosableFileFilter(filtro);
            archivo.setDialogTitle("Abrir imagen");
            int ventana = archivo.showOpenDialog(null);
            if (ventana == JFileChooser.APPROVE_OPTION) {

                file = archivo.getSelectedFile();
                foto = String.valueOf(file);

                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image1 = toolkit.getImage(foto);
                image1 = image1.getScaledInstance(250, 300, Image.SCALE_DEFAULT);
                c.labFoto.setIcon(new ImageIcon(image1));

            }
        }
        if (e.getSource() == mc.btnMod) {

            c.btnGuardar.setVisible(false);
            c.btnEdit.setVisible(true);
            c.fechaIngreso2.setVisible(false);

            filaEditar = mc.tabla1.getSelectedRow();
            numFila = mc.tabla1.getSelectedRowCount();

            if (filaEditar >= 0 && numFila == 1) {

                iniciarClientes();
                c.txtDni.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 0)));
                c.txtDni.setEditable(false);
                c.txtNombre.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 1)));
                c.fecha.setDate((Date) mc.tabla1.getValueAt(filaEditar, 2));
                c.txtNum.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 3)));
                c.txtDirec.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 4)));
                c.txtEmail.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 5)));

                try {
                    ImageIcon image1 = en.traerFoto((int) mc.tabla1.getValueAt(filaEditar, 0));
                    Icon icono = new ImageIcon(image1.getImage().getScaledInstance(250, 300, Image.SCALE_DEFAULT));

                    c.labFoto.setIcon(icono);

                } catch (SQLException ex) {
                    Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        if (e.getSource() == c.btnEdit) {
            if (c.txtDni.getText().length() == 0 || c.txtDirec.getText().length() == 0 || c.txtNombre.getText().length() == 0 || c.txtEmail.getText().length() == 0 || c.txtNum.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Error campos incompletos");

            }
            String nroTel = c.txtNum.getText().replaceAll(" ","");
            c.txtDni.setEditable(false);
            cl.setDni(Integer.parseInt(c.txtDni.getText()));
            cl.setNombre(c.txtNombre.getText());
            cl.setFechaNac(new java.sql.Date(c.fecha.getDate().getTime()));
            cl.setNroTel(Integer.parseInt(nroTel));
            cl.setDirec(c.txtDirec.getText());
            cl.setMail(c.txtEmail.getText());
            cl.setRuta(foto);

            try {
                if (c.txtNombre.getText().length() > 0) {
                    if (en.modificar(cl)) {

                        JOptionPane.showMessageDialog(null, "registro modificado");

                        modelo.setValueAt(cl.getDni(), filaEditar, 0);
                        modelo.setValueAt(cl.getNombre(), filaEditar, 1);
                        modelo.setValueAt(cl.getFechaNac(), filaEditar, 2);

                        modelo.setValueAt(cl.getNroTel(), filaEditar, 3);
                        modelo.setValueAt(cl.getDirec(), filaEditar, 4);
                        modelo.setValueAt(cl.getMail(), filaEditar, 5);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al modificar");

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error al modificar: Campo nombre incompleto");
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
            }

            c.txtDni.setEnabled(true);
            Image image1 = new ImageIcon(getClass().getResource("/Imagenes/perfil2.png")).getImage();
            image1.getScaledInstance(250, 300, Image.SCALE_DEFAULT);
            c.labFoto.setIcon(new ImageIcon(image1));
            c.dispose();

        }

        if (e.getSource() == c.btnCancel) {
            c.txtDni.setEnabled(true);
            Image image1 = new ImageIcon(getClass().getResource("/Imagenes/perfil2.png")).getImage();
            image1.getScaledInstance(250, 300, Image.SCALE_DEFAULT);
            c.labFoto.setIcon(new ImageIcon(image1));
            c.dispose();

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == mc.txtBuscar) {
            trs = new TableRowSorter(modelo);
            mc.tabla1.setRowSorter(trs);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == mc.txtBuscar) {
            if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {

                mc.txtBuscar.setText("");
            }

            if (e.getKeyChar() == KeyEvent.VK_0
                    || e.getKeyChar() == KeyEvent.VK_1
                    || e.getKeyChar() == KeyEvent.VK_2
                    || e.getKeyChar() == KeyEvent.VK_3
                    || e.getKeyChar() == KeyEvent.VK_4
                    || e.getKeyChar() == KeyEvent.VK_5
                    || e.getKeyChar() == KeyEvent.VK_6
                    || e.getKeyChar() == KeyEvent.VK_7
                    || e.getKeyChar() == KeyEvent.VK_8
                    || e.getKeyChar() == KeyEvent.VK_9) {

                trs.setRowFilter(RowFilter.regexFilter(mc.txtBuscar.getText(), 0));

            }
            if (!(e.getKeyChar() == KeyEvent.VK_0
                    || e.getKeyChar() == KeyEvent.VK_1
                    || e.getKeyChar() == KeyEvent.VK_2
                    || e.getKeyChar() == KeyEvent.VK_3
                    || e.getKeyChar() == KeyEvent.VK_4
                    || e.getKeyChar() == KeyEvent.VK_5
                    || e.getKeyChar() == KeyEvent.VK_6
                    || e.getKeyChar() == KeyEvent.VK_7
                    || e.getKeyChar() == KeyEvent.VK_8
                    || e.getKeyChar() == KeyEvent.VK_9)) {

                trs.setRowFilter(RowFilter.regexFilter("(?iu)" + mc.txtBuscar.getText(), 1));

            }

        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            int filaEditar = mc.tabla1.getSelectedRow();
            int numFila = mc.tabla1.getSelectedRowCount();
            ImageIcon image1 = null;

            Image image2 = new ImageIcon(getClass().getResource("/Imagenes/perfil2.png")).getImage();
            image2.getScaledInstance(250, 300, Image.SCALE_DEFAULT);
            mc.labFoto2.setIcon(new ImageIcon(image2));

            try {
                image1 = en.traerFoto((int) mc.tabla1.getValueAt(filaEditar, 0));
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (image1 != null) {
                Icon icono = new ImageIcon(image1.getImage().getScaledInstance(250, 300, Image.SCALE_DEFAULT));

                mc.labFoto2.setIcon(icono);

            }
        }
        if (e.getClickCount() == 2) {

            c.btnGuardar.setVisible(false);
            c.btnEdit.setVisible(true);
            c.fechaIngreso2.setVisible(false);

            filaEditar = mc.tabla1.getSelectedRow();
            numFila = mc.tabla1.getSelectedRowCount();

            if (filaEditar >= 0 && numFila == 1) {

                iniciarClientes();
                c.txtDni.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 0)));
                c.txtDni.setEditable(false);
                c.txtNombre.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 1)));
                c.fecha.setDate((Date) mc.tabla1.getValueAt(filaEditar, 2));
                c.txtNum.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 3)));
                c.txtDirec.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 4)));
                c.txtEmail.setText(String.valueOf(mc.tabla1.getValueAt(filaEditar, 5)));

                try {
                    ImageIcon image1 = en.traerFoto((int) mc.tabla1.getValueAt(filaEditar, 0));
                    Icon icono = new ImageIcon(image1.getImage().getScaledInstance(250, 300, Image.SCALE_DEFAULT));

                    c.labFoto.setIcon(icono);

                } catch (SQLException ex) {
                    Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ControladorClientes.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
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
