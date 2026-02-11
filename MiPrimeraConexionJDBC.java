/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.miprimeraconexionjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author LABORATORIOS
 */
public class MiPrimeraConexionJDBC 
{
    private static final String URL = "jdbc:mysql://localhost:3306/MyDataBase";
    private static final String USER = "root";
    private static final String PASS = "8134rgt!";
    
    public static Connection getConnection() throws SQLException 
    {
        return DriverManager.getConnection(URL, USER, PASS);
    }  
    
    public static void crearTabla()
    {
        String sql = "CREATE TABLE IF NOT EXISTS clientes ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "nombre VARCHAR(100),"
                + "password VARCHAR(100))";
        
        try(Connection con = getConnection();
             Statement st = con.createStatement())
        {
            st.execute(sql);
            System.out.println("Tabla creada con exito");
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();            
        }    
    }
    
    public static void obtenerClientes()
    {
        String sql = "SELECT * FROM clientes";
        
        try(
             Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            while(rs.next())
            {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                System.out.println(id + "-" + nombre    );
            }    
        }   
        catch(SQLException e)
        {
            e.printStackTrace();            
        } 
            
    }        

    public static boolean login(String nombre, String password)
    {
        String sql = "SELECT * FROM Clientes WHERE nombre ='" + nombre + "'AND  password='" + password + "'";
        try(
             Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            return rs.next();
        }   
        catch(SQLException e)
        {
            e.printStackTrace(); 
            return false;
        } 
    }  
    
    public static boolean loginSeguro(String nombre, String password)
    {
        String sql = "SELECT * FROM Clientes WHERE nombre=? AND  password=?";
        try(
             Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, nombre);
            ps.setString(2, password);
            try(ResultSet rs =  ps.executeQuery())
            {
                return rs.next();
            }    
           
        }   
        catch(SQLException e)
        {
            e.printStackTrace(); 
            return false;
        } 
    } 
    
    public static void insertar(String nombre, String password) 
    {
        String sql = "INSERT INTO clientes (nombre, password) VALUES (?, ?)";
        
        try(
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            ps.setString(1, nombre);
            ps.setString(2, password);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) 
            {
                if (rs.next()) 
                {
                    System.out.println("Insertado cliente: " + nombre + " con ID: " + rs.getInt(1));
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public static void obtenerClientePorId(int id) 
    {
        String sql = "SELECT id, nombre, password FROM clientes WHERE id = ?";       
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, id);
            
            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    System.out.println("ID: " + rs.getInt("id")
                            + ", Nombre: " + rs.getString("nombre")
                            + ", Password: " + rs.getString("password")
                    );
                }
                else
                {
                    System.out.println("No se encontro cliente con ID: " + id);
                }    
            }    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void actualizar(int id, String nuevoNombre, String nuevoPassword) 
    {
        String sql = "UPDATE clientes SET nombre = ?, password = ? WHERE id = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) 
        {
            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevoPassword);
            ps.setInt(3, id);
            
            int filas = ps.executeUpdate();
            if (filas > 0) 
            {
                System.out.println("Cliente actualizado con ID: " + id);
            } 
            else 
            {
                System.out.println("No se encontró cliente para actualizar con ID: " + id);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public static void eliminar(int id) 
    {
        String sql = "DELETE FROM clientes WHERE id = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) 
        {
            
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            
            if (filas > 0) 
            {
                System.out.println("Cliente eliminado con ID: " + id);
            } 
            else 
            {
                System.out.println("No se encontró cliente para eliminar con ID: " + id);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {

        // 1. Crear tabla
        crearTabla();

        // 2. Insertar clientes de prueba
        System.out.println("\n--- Inserciones ---");
        insertar("Juan", "1234");
        insertar("Maria", "abcd");
        insertar("Pedro", "qwerty");
        insertar("Ana", "pass123");

        // 3. Leer todos los clientes
        System.out.println("\n--- Todos los clientes ---");
        obtenerClientes();

        // 4. Leer cliente por ID
        System.out.println("\n--- Consulta por ID ---");
        obtenerClientePorId(1); // Suponiendo que Juan tiene ID 1
        obtenerClientePorId(3); // Suponiendo que Pedro tiene ID 3
        obtenerClientePorId(99); // ID inexistente, debe indicar que no se encontró

        // 5. Actualizar cliente
        System.out.println("\n--- Actualización ---");
        actualizar(2, "Maria Actualizada", "newpass"); // Cambiamos datos de Maria
        obtenerClientePorId(2); // Verificamos cambios

        // 6. Eliminar cliente
        System.out.println("\n--- Eliminación ---");
        eliminar(3); // Eliminamos a Pedro
        obtenerClientes(); // Verificamos que Pedro ya no aparece

        // 7. Pruebas de login
        System.out.println("\n--- Login normal ---");
        System.out.println("Login Juan/1234: " + login("Juan", "1234"));       // true
        System.out.println("Login Ana/pass123: " + login("Ana", "pass123"));   // true
        System.out.println("Login incorrecto: " + login("Ana", "wrong"));      // false

        System.out.println("\n--- Login seguro ---");
        System.out.println("Login seguro Juan/1234: " + loginSeguro("Juan", "1234"));     // true
        System.out.println("Login seguro Ana/pass123: " + loginSeguro("Ana", "pass123")); // true
        System.out.println("Login seguro incorrecto: " + loginSeguro("Ana", "wrong"));     // false

        // 8. Prueba de SQL Injection
        System.out.println("\n--- Prueba SQL Injection ---");
        System.out.println("Intento login normal: " + login("Juan", "' OR '1'='1")); // Puede fallar (dar true siendo hackeo)
        System.out.println("Intento login seguro: " + loginSeguro("Juan", "' OR '1'='1")); // Siempre false
    }

}
