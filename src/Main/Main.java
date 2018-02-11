/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Controlador.ControladorClientes;
import Controlador.ControladorMenu;
import Controlador.ControladorPago;
import Controlador.ControladorReportes;
import Controlador.ControladorTarifas;
import Modelo.Cliente;
import Modelo.Conexion;
import Modelo.Cuenta;
import Modelo.EntidadCliente;
import Modelo.EntidadCuentaCorriente;
import Modelo.EntidadTarifa;
import Modelo.Pagos;
import Modelo.Tarifa;
import Modelo.MiModelo;
import Vista.Clientes;
import Vista.Menu;
import Vista.MenuClientes;
import Vista.Tarifas;
import Vista.VistaPagos;
import Vista.VistaReportes;



/**
 *
 * @author JorgeA
 */
public class Main {
    
    public static void main(String[] args){
        
        Clientes cl = new Clientes();
        Menu m = new Menu();
        Cliente c1 = new Cliente();
        Conexion cn = new Conexion();
        EntidadCliente en = new EntidadCliente();
        MenuClientes mc = new MenuClientes();
        Tarifas tf = new Tarifas();
        Tarifa ta = new Tarifa();
        EntidadTarifa et = new EntidadTarifa();
        MiModelo mm = new MiModelo();
        ControladorTarifas ct = new ControladorTarifas(tf,et,ta,mm);
        Cuenta cu = new Cuenta();
        EntidadCuentaCorriente ec = new EntidadCuentaCorriente();
        Pagos p = new Pagos();
        VistaPagos vp = new VistaPagos();
        VistaReportes vr = new VistaReportes();
        
        
        ControladorReportes cr = new ControladorReportes(vr);
        ControladorClientes col = new ControladorClientes(cl,c1,en,mc,cu,et,ec,mm);
        ControladorPago cp = new ControladorPago(p,vp,cu,ec,mm);
        ControladorMenu com = new ControladorMenu(col,cr,m,ct,cp,mm);
        
        com.iniciar();
      
        
        
    }
}
