/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author JorgeA
 */
public class EntidadCliente extends Conexion {

    public boolean registrar(Cliente c) throws ClassNotFoundException, FileNotFoundException {
        PreparedStatement ps = null;
        FileInputStream fis = null;
        Connection con = getConect();
        String sql = "INSERT INTO clientes (dni,nombre,fechaNac,nroTel,direccion,mail,ruta,imagen,fechaVal,fechaVto,baja) VALUES (?,?,?,?,?,?,?,?,?,?,true)";
        String sql2 = "INSERT INTO clientes (dni,nombre,fechaNac,nroTel,direccion,mail,fechaVal,fechaVto,baja) VALUES (?,?,?,?,?,?,?,?,true)";
        
        try {
            int i=0;
            if (c.getRuta() == null) {
                
                ps = con.prepareStatement(sql2);
                ps.setInt(1, c.getDni());
                ps.setString(2, c.getNombre());
                ps.setDate(3, c.getFechaNac());
                ps.setInt(4, c.getNroTel());
                ps.setString(5, c.getDirec());
                ps.setString(6, c.getMail());
                ps.setDate(7, c.getFechaVal());
                ps.setDate(8, c.getFechaVto());
                
                System.out.println(i);
                
                ps.execute();
                
            } else {
                
                File archivo;
                archivo = new File(c.getRuta());
                fis = new FileInputStream(archivo);
                
                ps = con.prepareStatement(sql);
                ps.setInt(1, c.getDni());
                ps.setString(2, c.getNombre());
                ps.setDate(3, c.getFechaNac());
                ps.setInt(4, c.getNroTel());
                ps.setString(5, c.getDirec());
                ps.setString(6, c.getMail());
                ps.setString(7, c.getRuta());
                ps.setBinaryStream(8, fis, (int) archivo.length());
                ps.setDate(9, c.getFechaVal());
                ps.setDate(10, c.getFechaVto());
                

                ps.execute();
                
            }
            return true;
        } catch (SQLException ex) {
            
            return false;
            

        } finally {
            try {
                con.close();

            } catch (SQLException ex) {
                System.err.println(ex);
            }

        }
    }

    public boolean modificar(Cliente c) throws FileNotFoundException, ClassNotFoundException {
        PreparedStatement ps = null;
        FileInputStream fis = null;
        Connection con = getConect();
        String sql = "UPDATE clientes SET nombre=?, fechaNac=?, nroTel=?, direccion=?, mail=?, ruta=?, imagen=? WHERE dni=?";
        String sql2 = "UPDATE clientes SET nombre=?, fechaNac=?, nroTel=?, direccion=?, mail=? WHERE dni=?";

        try {
            if (c.getRuta() == null) {
                ps = con.prepareStatement(sql2);
                ps.setInt(6, c.getDni());
                ps.setString(1, c.getNombre());
                ps.setDate(2, c.getFechaNac());
                ps.setInt(3, c.getNroTel());
                ps.setString(4, c.getDirec());
                ps.setString(5, c.getMail());

                System.out.println(ps);
                ps.execute();

            } else {
                File archivo;
                archivo = new File(c.getRuta());
                fis = new FileInputStream(archivo);
                ps = con.prepareStatement(sql);
                ps.setInt(8, c.getDni());
                ps.setString(1, c.getNombre());
                ps.setDate(2, c.getFechaNac());
                ps.setInt(3, c.getNroTel());
                ps.setString(4, c.getDirec());
                ps.setString(5, c.getMail());
                ps.setString(6, c.getRuta());
                ps.setBinaryStream(7, fis, (int) archivo.length());
                
                ps.execute();

            }
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
            return false;

        } finally {
            try {
                con.close();

            } catch (SQLException ex) {
                System.err.println(ex);
            }

        }
    }

    public ImageIcon traerFoto(int dni) throws SQLException, ClassNotFoundException, IOException {
        ImageIcon ii = null;
        InputStream is = null;

        String sql = "SELECT imagen FROM clientes WHERE dni= " + dni;
        Connection con = getConect();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            is = rs.getBinaryStream(1);
            BufferedImage bi = ImageIO.read(is);
            ii = new ImageIcon(bi);
        }

        return ii;

    }

    /*public boolean eliminar(Cliente c) throws FileNotFoundException, ClassNotFoundException {
        PreparedStatement ps = null;
        FileInputStream fis = null;
        Connection con = getConect();
        String sql = "DELETE FROM clientes WHERE dni=?";

        try {
            if (c.getRuta() == null) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, c.getDni());

                ps.execute();

            } else {
                File archivo;
                archivo = new File(c.getRuta());
                fis = new FileInputStream(archivo);
                ps = con.prepareStatement(sql);
                ps.setInt(8, c.getDni());
                ps.setString(1, c.getNombre());
                ps.setDate(2, c.getFechaNac());
                ps.setInt(3, c.getNroTel());
                ps.setString(4, c.getDirec());
                ps.setString(5, c.getMail());
                ps.setString(6, c.getRuta());
                ps.setBinaryStream(7, fis, (int) archivo.length());
                ps.execute();

            }
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
            return false;

        } finally {
            try {
                con.close();

            } catch (SQLException ex) {
                System.err.println(ex);
            }

        }
    }*/
}
