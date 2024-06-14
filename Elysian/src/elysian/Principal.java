/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elysian;


//BDD
import com.itextpdf.text.BaseColor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//C:\Users\j0c3lwiz\Desktop\Elysian.accdb
//Visuales
import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Random;



/**
 *
 * @author j0c3lwiz
 */
public class Principal extends javax.swing.JFrame {


    private static String DB_URL = "jdbc:ucanaccess://src/databases/Elysian.accdb";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    
    
    
    // Variables para la conexión a la base de datos
    private Connection conn;
    
    // Método para autenticar al usuario
    private boolean authenticate(String email, String password) {
        String query = "SELECT * FROM Usuarios WHERE Correo = ? AND Contraseña = ? AND Estado = 'Activado'";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Si hay un resultado, la autenticación es exitosa

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para actualizar la ruta de la base de datos
    private void updateDatabasePath(String dbPath) {
        // Cierra cualquier conexión anterior si es necesario
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Actualiza la conexión con la nueva ruta de la base de datos
        DB_URL = "jdbc:ucanaccess://" + dbPath;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            JOptionPane.showMessageDialog(this, "Database path updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update database path!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void insertUser(String nombre, String apellido, String correo, String contra, String estado) {
        if (isEmailRegistered(correo)) {
            JOptionPane.showMessageDialog(this, "El correo ya está registrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Usuarios (Nombre, Apellido, Correo, Contraseña, Estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, correo);
            stmt.setString(4, contra);
            stmt.setString(5, estado);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente");
            users_name.setText("");
            users_apellido.setText("");
            users_correo.setText("");
            users_password.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 

       private void generateReceipt() throws DocumentException, IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar recibo");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Agregar logo de la empresa
            Image logo = Image.getInstance("src/elysian/images/login(3).jpg");
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.scaleToFit(100, 100);
            document.add(logo);

            // Nombre de la empresa
            Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph companyName = new Paragraph("ElysianHN", boldFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);

            // Fecha

            document.add(new Paragraph("Fecha: " + "6/14/2024"));

            // Número de contacto
            document.add(new Paragraph("Contacto: +504 8759-6371"));

            // Línea en blanco
            document.add(new Paragraph(" "));

            // Nombre del cliente
            String cliente = clients_cbname.getSelectedItem().toString();
            document.add(new Paragraph("Cliente: " + cliente));
            document.add(new Paragraph(" ")); // Línea en blanco

            // Tabla de productos
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Encabezados de la tabla
            PdfPCell cell1 = new PdfPCell(new Phrase("Producto"));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell cell2 = new PdfPCell(new Phrase("Categoria"));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell cell3 = new PdfPCell(new Phrase("Material"));
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell cell4 = new PdfPCell(new Phrase("Precio"));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);

            // Datos de la tabla
            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            int rowCount = model.getRowCount();

            double total = 0;
            for (int i = 0; i < rowCount; i++) {
                String producto = model.getValueAt(i, 0).toString();
                String categoria = model.getValueAt(i, 1).toString();
                String material = model.getValueAt(i, 2).toString();
                double precio = Double.parseDouble(model.getValueAt(i, 3).toString());

                table.addCell(producto);
                table.addCell(categoria);
                table.addCell(material);
                table.addCell(String.format("%.2f", precio));

                total += precio;
            }

            document.add(table);

            // Total
            document.add(new Paragraph("Total: " + String.format("%.2f", total)));

            document.close();

            JOptionPane.showMessageDialog(this, "Recibo generado exitosamente");
        }
    }



    private boolean isEmailRegistered(String email) {
        String query = "SELECT COUNT(*) FROM Usuarios WHERE Correo = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void loadUserData() {
    String query = "SELECT * FROM Usuarios";
    
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) jTable5.getModel();
            model.setRowCount(0); // Limpiar la tabla antes de cargar datos

            while (rs.next()) {
                int id = rs.getInt("id");
                String correo = rs.getString("Correo");
                String contrasena = rs.getString("Contraseña");
                String nombre = rs.getString("Nombre");
                String apellido = rs.getString("Apellido");
                String estado = rs.getString("Estado");

                model.addRow(new Object[]{id, correo, contrasena, nombre, apellido, estado});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteUser(int id) {
        String query = "DELETE FROM Usuarios WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveChanges() {
        DefaultTableModel model = (DefaultTableModel) jTable5.getModel();
        int rowCount = model.getRowCount();

        String query = "UPDATE Usuarios SET Correo = ?, Contraseña = ?, Nombre = ?, Apellido = ?, Estado = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < rowCount; i++) {
                int id = (int) model.getValueAt(i, 0);
                String correo = (String) model.getValueAt(i, 1);
                String contrasena = (String) model.getValueAt(i, 2);
                String nombre = (String) model.getValueAt(i, 3);
                String apellido = (String) model.getValueAt(i, 4);
                String estado = (String) model.getValueAt(i, 5);

                stmt.setString(1, correo);
                stmt.setString(2, contrasena);
                stmt.setString(3, nombre);
                stmt.setString(4, apellido);
                stmt.setString(5, estado);
                stmt.setInt(6, id);

                stmt.addBatch();
            }

            stmt.executeBatch();
            JOptionPane.showMessageDialog(this, "Cambios guardados exitosamente");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar los cambios", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
      private void loadProductData() {
        String query = "SELECT * FROM Productos";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) jTable6.getModel();
            model.setRowCount(0); // Limpiar la tabla antes de cargar datos

            while (rs.next()) {
                int id = rs.getInt("id"); // Asumiendo que el campo de id es un entero
                String producto = rs.getString("Producto");
                double precio = rs.getDouble("Precio");
                String categoria = rs.getString("Categoria");
                String material = rs.getString("Material");

                model.addRow(new Object[]{id, producto, precio, categoria, material});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

        
    private void insertProduct(String nombre, String precio, String categoria, String material) {
        String query = "INSERT INTO Productos (Producto, Precio, Categoria, Material) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, precio);
            stmt.setString(3, categoria);
            stmt.setString(4, material);

            stmt.executeUpdate();
            product_name.setText("");
            product_categoria.setSelectedIndex(0);
            product_material.setSelectedIndex(0);
            product_price.setValue(0);
            JOptionPane.showMessageDialog(this, "Producto guardado exitosamente");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar el producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
   private void saveProductChanges() {
        DefaultTableModel model = (DefaultTableModel) jTable6.getModel();
        int rowCount = model.getRowCount();

        String query = "UPDATE Productos SET Producto = ?, Precio = ?, Categoria = ?, Material = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < rowCount; i++) {
                int id = (int) model.getValueAt(i, 0); // Asumiendo que la columna 0 es el ID y es de tipo Integer
                String producto = (String) model.getValueAt(i, 1); // Producto (String)
                double precio = Double.parseDouble(model.getValueAt(i, 2).toString()); // Precio (double)
                String categoria = (String) model.getValueAt(i, 3); // Categoria (String)
                String material = (String) model.getValueAt(i, 4); // Material (String)

                stmt.setString(1, producto);
                stmt.setDouble(2, precio);
                stmt.setString(3, categoria);
                stmt.setString(4, material);
                stmt.setInt(5, id);

                stmt.addBatch();
            }

            stmt.executeBatch();
            JOptionPane.showMessageDialog(this, "Cambios guardados exitosamente");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar los cambios", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
      private void deleteProduct(int id) {
         String query = "DELETE FROM Productos WHERE id = ?";

         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
              PreparedStatement stmt = conn.prepareStatement(query)) {

             stmt.setInt(1, id);
             int rowsAffected = stmt.executeUpdate();

             if (rowsAffected > 0) {
                 JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente");
             } else {
                 JOptionPane.showMessageDialog(this, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
             }

         } catch (SQLException e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
         }
     }
      
      private void insertClient(String nombre, String apellido, String categoria, int descuento) {
        String query = "INSERT INTO Clientes (Nombre, Apellido, Categoria, Descuento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, categoria);
            stmt.setInt(4, descuento);

            stmt.executeUpdate();
            clientes_nombre.setText("");
            clientes_apellido.setText("");
            clientes_categoria.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this, "Cliente guardado exitosamente");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
      
      private void loadClientData() {
        String query = "SELECT * FROM Clientes";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) jTable7.getModel();
            model.setRowCount(0); // Limpiar la tabla antes de cargar datos

            while (rs.next()) {
                int id = rs.getInt("id"); // Asumiendo que tienes una columna de ID
                String nombre = rs.getString("Nombre");
                String apellido = rs.getString("Apellido");
                String categoria = rs.getString("Categoria");
                int descuento = rs.getInt("Descuento");

                model.addRow(new Object[]{id, nombre, apellido, categoria, descuento});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
      
      private void saveClientChanges() {
        DefaultTableModel model = (DefaultTableModel) jTable7.getModel();
        int rowCount = model.getRowCount();

        String query = "UPDATE Clientes SET Nombre = ?, Apellido = ?, Categoria = ?, Descuento = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < rowCount; i++) {
                int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                String nombre = model.getValueAt(i, 1).toString();
                String apellido = model.getValueAt(i, 2).toString();
                String categoria = model.getValueAt(i, 3).toString();
                int descuento = Integer.parseInt(model.getValueAt(i, 4).toString());

                stmt.setString(1, nombre);
                stmt.setString(2, apellido);
                stmt.setString(3, categoria);
                stmt.setInt(4, descuento);
                stmt.setInt(5, id);

                stmt.addBatch();
            }

            stmt.executeBatch();
            JOptionPane.showMessageDialog(this, "Cambios guardados exitosamente");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar los cambios", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
      
      private void deleteClient(int id) {
        String query = "DELETE FROM Clientes WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
      
      private void loadClientNames() {
        String query = "SELECT Nombre FROM Clientes";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            clients_cbname.removeAllItems(); // Limpiar el combo box antes de cargar datos

            while (rs.next()) {
                String nombre = rs.getString("Nombre");
                clients_cbname.addItem(nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los nombres de los clientes", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductNames() {
        String query = "SELECT Producto FROM Productos";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            clients_cbproduct.removeAllItems(); // Limpiar el combo box antes de cargar datos

            while (rs.next()) {
                String producto = rs.getString("Producto");
                clients_cbproduct.addItem(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los nombres de los productos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void addItemToTable() {
        String selectedItem = clients_cbproduct.getSelectedItem().toString();

        String query = "SELECT Producto, Categoria, Material, Precio FROM Productos WHERE Producto = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, selectedItem);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String producto = rs.getString("Producto");
                String categoria = rs.getString("Categoria");
                String material = rs.getString("Material");
                double precio = rs.getDouble("Precio");

                DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
                model.addRow(new Object[]{producto, categoria, material, precio});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al agregar el producto a la tabla", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotal() {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        int rowCount = model.getRowCount();
        double total = 0;

        for (int i = 0; i < rowCount; i++) {
            total += (double) model.getValueAt(i, 3); // Asumiendo que la columna 3 es el precio
        }

        clients_total.setText(String.format("%.2f", total));
    }
    // Lista estática para almacenar los diálogos abiertos
    
    private void deleteSelectedRow() {
    int selectedRow = jTable3.getSelectedRow();
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
    model.removeRow(selectedRow);
}
        private void loadOrderData() {
        String query = "SELECT * FROM ORDENES";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // Limpiar la tabla antes de cargar datos

            while (rs.next()) {
                int id = rs.getInt("id"); // Asumiendo que 'id' es de tipo INTEGER en la base de datos
                String codigo = rs.getString("codigo"); // Asegúrate de que este campo sea VARCHAR en la base de datos
                String listaProductos = rs.getString("listaproductos");
                double costo = rs.getDouble("costo");
                String estado = rs.getString("estado");

                model.addRow(new Object[]{id, codigo, listaProductos, costo, estado});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        
        private void saveNewOrder() {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        int rowCount = model.getRowCount();

        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "No hay artículos en la lista", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder listaProductos = new StringBuilder();
        double total = 0;

        for (int i = 0; i < rowCount; i++) {
            String producto = model.getValueAt(i, 0).toString();
            double precio = Double.parseDouble(model.getValueAt(i, 3).toString());

            listaProductos.append(producto);
            if (i < rowCount - 1) {
                listaProductos.append(", ");
            }

            total += precio;
        }

        String codigo = generateRandomCode();
        String estado = "No entregado";

        // Guardar la nueva orden en la base de datos
        String query = "INSERT INTO ORDENES (codigo, listaproductos, costo, estado) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codigo);
            stmt.setString(2, listaProductos.toString());
            stmt.setDouble(3, total);
            stmt.setString(4, estado);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Orden guardada exitosamente");

            // Vaciar la tabla después de guardar la orden
            model.setRowCount(0);
            clients_total.setText(String.format("%.2f", 0.0));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar la orden", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private String generateRandomCode() {
        int length = 8;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

       private void showSelectedOrderDetails() {
        int selectedRow = jTable2.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una orden para ver los detalles", "Detalles de Órdenes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        int columnCount = model.getColumnCount();

        StringBuilder details = new StringBuilder();
        details.append("Detalles de la Orden Seleccionada:\n\n");

        for (int i = 0; i < columnCount; i++) {
            String columnName = model.getColumnName(i);
            String value = model.getValueAt(selectedRow, i).toString();
            if (columnName.equals("listaproductos")) {
                details.append(columnName).append(":").append("\n");
                String[] productos = value.split(", ");
                for (int j = 0; j < productos.length; j++) {
                    details.append((j + 1)).append(". ").append(productos[j]).append("\n");
                }
            } else {
                details.append(columnName).append(": ").append(value).append("\n");
            }
        }

        JOptionPane.showMessageDialog(this, details.toString(), "Detalles de Órdenes", JOptionPane.INFORMATION_MESSAGE);
    }

       private void deleteSelectedOrder() {
        int selectedRow = jTable2.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una orden para eliminar", "Eliminar Orden", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        int orderId = (int) model.getValueAt(selectedRow, 0); // Asumiendo que la primera columna es el ID

        // Eliminar la orden de la base de datos
        String query = "DELETE FROM ORDENES WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Eliminar la fila del modelo de tabla
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Orden eliminada exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la orden", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar la orden", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

       private void toggleOrderStatus() {
        int selectedRow = jTable2.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una orden para cambiar su estado", "Cambiar Estado de Orden", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        int orderId = (int) model.getValueAt(selectedRow, 0); // Asumiendo que la primera columna es el ID
        String currentStatus = model.getValueAt(selectedRow, 4).toString(); // Asumiendo que la cuarta columna es el estado
        String newStatus = currentStatus.equals("Entregada") ? "No Entregada" : "Entregada";

        // Actualizar el estado de la orden en la base de datos
        String query = "UPDATE ORDENES SET estado = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Actualizar el estado en el modelo de tabla
                model.setValueAt(newStatus, selectedRow, 4);
                JOptionPane.showMessageDialog(this, "Estado de la orden cambiado exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cambiar el estado de la orden", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cambiar el estado de la orden", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     private void showContactAdminMessage() {
    JOptionPane.showMessageDialog(this, "Contacta al administrador: Elysianhn@gmail.com", "Contacto", JOptionPane.INFORMATION_MESSAGE);
     }
       
    private void saveCredentials(String email, String password) {
    // Guardar el email y la contraseña en los campos
    jTextField4.setText(email);
    jPasswordField1.setText(password);
}

    private void clearCredentials() {
        // Borrar el email y la contraseña de los campos
        jTextField4.setText("");
        jPasswordField1.setText("");
    }



    private static ArrayList<JDialog> dialogList = new ArrayList<>();

    // Método para añadir un diálogo a la lista
    public static void addDialog(JDialog dialog) {
        dialogList.add(dialog);
    }

    // Método para cerrar todos los diálogos
    public static void closeAllDialogs() {
        for (JDialog dialog : dialogList) {
            dialog.dispose();
        }
        dialogList.clear();
    }
    /**
     * Creates new form Main
     */
    public Principal() {
        initComponents();
        setLocationRelativeTo(null);
        
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Landingpage = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        Users = new javax.swing.JDialog();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanellp = new javax.swing.JPanel();
        jToggleButton2 = new javax.swing.JToggleButton();
        users_name = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        users_apellido = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        users_correo = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        userscombo_estado = new javax.swing.JComboBox<>();
        users_password = new javax.swing.JPasswordField();
        jPanellp5 = new javax.swing.JPanel();
        jToggleButton15 = new javax.swing.JToggleButton();
        jLabel48 = new javax.swing.JLabel();
        borrar_table = new javax.swing.JToggleButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        Inventory = new javax.swing.JDialog();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanellp1 = new javax.swing.JPanel();
        product_name = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        product_categoria = new javax.swing.JComboBox<>();
        product_material = new javax.swing.JComboBox<>();
        jToggleButton4 = new javax.swing.JToggleButton();
        product_price = new javax.swing.JSpinner();
        jPanellp7 = new javax.swing.JPanel();
        jToggleButton16 = new javax.swing.JToggleButton();
        jLabel45 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jToggleButton19 = new javax.swing.JToggleButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        Orders = new javax.swing.JDialog();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanellp3 = new javax.swing.JPanel();
        clients_total = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jToggleButton10 = new javax.swing.JToggleButton();
        jToggleButton11 = new javax.swing.JToggleButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jToggleButton12 = new javax.swing.JToggleButton();
        clients_cbproduct = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
        jToggleButton13 = new javax.swing.JToggleButton();
        jLabel41 = new javax.swing.JLabel();
        clients_cbname = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        jPanellp2 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        jToggleButton6 = new javax.swing.JToggleButton();
        jToggleButton7 = new javax.swing.JToggleButton();
        jToggleButton8 = new javax.swing.JToggleButton();
        Clientes = new javax.swing.JDialog();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanellp4 = new javax.swing.JPanel();
        clientes_nombre = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        clientes_apellido = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        clientes_categoria = new javax.swing.JComboBox<>();
        jToggleButton9 = new javax.swing.JToggleButton();
        clientes_descuento = new javax.swing.JLabel();
        jPanellp8 = new javax.swing.JPanel();
        jToggleButton17 = new javax.swing.JToggleButton();
        jLabel47 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jToggleButton21 = new javax.swing.JToggleButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        Settings = new javax.swing.JDialog();
        jPanellp6 = new javax.swing.JPanel();
        jTextField10 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jToggleButton18 = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jPanel4 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jToggleButton1.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton1.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/usersrzs.png"))); // NOI18N
        jToggleButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseClicked(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/productrsz.png"))); // NOI18N
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/clientsrsz.png"))); // NOI18N
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/orderrsz.png"))); // NOI18N
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/lout.png"))); // NOI18N
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/login(2).jpg"))); // NOI18N

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel9.setText("¡Bienvenido!");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel10.setText(" ¡Simplifica tu gestión de inventario!");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel11.setText("Optimiza procesos y aumenta la eficiencia. ");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel12.setText("¡Descubre cómo hoy mismo!");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel13.setText("Tu contenido se mostrará aquí.");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(184, 184, 184)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(82, 82, 82))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(42, 42, 42))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(12, 12, 12)))
                        .addGap(110, 110, 110))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel9)
                .addGap(35, 35, 35)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addGap(85, 85, 85)
                .addComponent(jLabel13)
                .addContainerGap(232, Short.MAX_VALUE))
        );

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/settingsz.png"))); // NOI18N
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton6MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jToggleButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addGap(10, 10, 10)
                        .addComponent(jButton5)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout LandingpageLayout = new javax.swing.GroupLayout(Landingpage.getContentPane());
        Landingpage.getContentPane().setLayout(LandingpageLayout);
        LandingpageLayout.setHorizontalGroup(
            LandingpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        LandingpageLayout.setVerticalGroup(
            LandingpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane2MouseClicked(evt);
            }
        });

        jPanellp.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jToggleButton2.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton2.setText("Guardar Usuario");
        jToggleButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton2MouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel14.setText("Registro de Usuarios");

        jLabel15.setText("Apellido:");

        jLabel16.setText("Nombre:");

        jLabel17.setText("Correo:");

        jLabel19.setText("Contraseña:");

        jLabel20.setText("Estado:");

        userscombo_estado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activado", "Desactivado" }));

        javax.swing.GroupLayout jPanellpLayout = new javax.swing.GroupLayout(jPanellp);
        jPanellp.setLayout(jPanellpLayout);
        jPanellpLayout.setHorizontalGroup(
            jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellpLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellpLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userscombo_estado, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(150, 150, 150))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellpLayout.createSequentialGroup()
                        .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(191, 191, 191))))
            .addGroup(jPanellpLayout.createSequentialGroup()
                .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellpLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanellpLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(users_name))
                            .addGroup(jPanellpLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(19, 19, 19)
                                .addComponent(users_correo, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addGap(18, 18, 18)
                        .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(users_apellido, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                            .addComponent(users_password)))
                    .addGroup(jPanellpLayout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addComponent(jLabel14)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanellpLayout.setVerticalGroup(
            jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellpLayout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addComponent(jLabel14)
                .addGap(99, 99, 99)
                .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(users_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(users_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(users_correo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(users_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addGroup(jPanellpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(userscombo_estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
        );

        jTabbedPane2.addTab("Registrar", jPanellp);

        jPanellp5.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jToggleButton15.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton15.setText("Modificar Usuario");
        jToggleButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton15MouseClicked(evt);
            }
        });

        borrar_table.setBackground(new java.awt.Color(255, 255, 255));
        borrar_table.setText("Eliminar Usuario");
        borrar_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                borrar_tableMouseClicked(evt);
            }
        });

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Correo", "Contraseña", "Nombre", "Apellido", "Estado"
            }
        ));
        jScrollPane5.setViewportView(jTable5);

        javax.swing.GroupLayout jPanellp5Layout = new javax.swing.GroupLayout(jPanellp5);
        jPanellp5.setLayout(jPanellp5Layout);
        jPanellp5Layout.setHorizontalGroup(
            jPanellp5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp5Layout.createSequentialGroup()
                .addGap(279, 279, 279)
                .addComponent(jLabel48)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp5Layout.createSequentialGroup()
                .addGap(0, 339, Short.MAX_VALUE)
                .addComponent(jToggleButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
            .addGroup(jPanellp5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanellp5Layout.createSequentialGroup()
                    .addGap(85, 85, 85)
                    .addComponent(borrar_table, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(354, Short.MAX_VALUE)))
        );
        jPanellp5Layout.setVerticalGroup(
            jPanellp5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp5Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jToggleButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
            .addGroup(jPanellp5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp5Layout.createSequentialGroup()
                    .addContainerGap(539, Short.MAX_VALUE)
                    .addComponent(borrar_table, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(54, 54, 54)))
        );

        jTabbedPane2.addTab("Manejo de usuarios", jPanellp5);

        javax.swing.GroupLayout UsersLayout = new javax.swing.GroupLayout(Users.getContentPane());
        Users.getContentPane().setLayout(UsersLayout);
        UsersLayout.setHorizontalGroup(
            UsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        UsersLayout.setVerticalGroup(
            UsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        jTabbedPane3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane3MouseClicked(evt);
            }
        });

        jPanellp1.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel21.setText("Inventario");

        jLabel22.setText("Categoria:");

        jLabel23.setText("Producto:");

        jLabel24.setText("Precio:");

        jLabel27.setText("Material:");

        product_categoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aritos", "Pulsera", "Cadena" }));

        product_material.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Oro", "Plata", "Acero" }));

        jToggleButton4.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton4.setText("Guardar");
        jToggleButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton4MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanellp1Layout = new javax.swing.GroupLayout(jPanellp1);
        jPanellp1.setLayout(jPanellp1Layout);
        jPanellp1Layout.setHorizontalGroup(
            jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp1Layout.createSequentialGroup()
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanellp1Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(product_material, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanellp1Layout.createSequentialGroup()
                                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(product_name)
                                    .addComponent(product_categoria, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(product_price, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25))
                    .addGroup(jPanellp1Layout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(jLabel21)))
                .addContainerGap(66, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jToggleButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(154, 154, 154))
        );
        jPanellp1Layout.setVerticalGroup(
            jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp1Layout.createSequentialGroup()
                .addGap(285, 285, 285)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanellp1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(product_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(39, 39, 39)
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(product_price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(product_categoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(product_material, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(103, 103, 103)
                .addComponent(jToggleButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );

        jTabbedPane3.addTab("Guardar Producto", jPanellp1);

        jPanellp7.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jToggleButton16.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton16.setText("Modificar");
        jToggleButton16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton16MouseClicked(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel45.setText("Inventario");

        jToggleButton19.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton19.setText("Eliminar");
        jToggleButton19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton19MouseClicked(evt);
            }
        });

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Producto", "Precio", "Categoria", "Material"
            }
        ));
        jScrollPane6.setViewportView(jTable6);

        javax.swing.GroupLayout jPanellp7Layout = new javax.swing.GroupLayout(jPanellp7);
        jPanellp7.setLayout(jPanellp7Layout);
        jPanellp7Layout.setHorizontalGroup(
            jPanellp7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp7Layout.createSequentialGroup()
                .addGroup(jPanellp7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp7Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(jLabel52))
                    .addGroup(jPanellp7Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jToggleButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jToggleButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanellp7Layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(jLabel45)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanellp7Layout.setVerticalGroup(
            jPanellp7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp7Layout.createSequentialGroup()
                .addGap(285, 285, 285)
                .addComponent(jLabel52)
                .addGap(18, 376, Short.MAX_VALUE))
            .addGroup(jPanellp7Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel45)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addGroup(jPanellp7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58))
        );

        jTabbedPane3.addTab("Manejar Producto", jPanellp7);

        javax.swing.GroupLayout InventoryLayout = new javax.swing.GroupLayout(Inventory.getContentPane());
        Inventory.getContentPane().setLayout(InventoryLayout);
        InventoryLayout.setHorizontalGroup(
            InventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InventoryLayout.createSequentialGroup()
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 522, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        InventoryLayout.setVerticalGroup(
            InventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InventoryLayout.createSequentialGroup()
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 697, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanellp3.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        clients_total.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        clients_total.setText("x");

        jLabel36.setText("Producto:");

        jToggleButton10.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton10.setText("Añadir Articulo");
        jToggleButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton10MouseClicked(evt);
            }
        });

        jToggleButton11.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton11.setText("Guardar Orden");
        jToggleButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton11MouseClicked(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Categoria", "Material", "Precio"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jToggleButton12.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton12.setText("Generar Recibo");
        jToggleButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton12MouseClicked(evt);
            }
        });

        clients_cbproduct.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cadena de oro", "Cadena de plata" }));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel40.setText("Crear Orden");

        jToggleButton13.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton13.setText("Eliminar Articulo");
        jToggleButton13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton13MouseClicked(evt);
            }
        });

        jLabel41.setText("Cliente:");

        clients_cbname.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jorge Gonzales" }));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel35.setText("Total:");

        javax.swing.GroupLayout jPanellp3Layout = new javax.swing.GroupLayout(jPanellp3);
        jPanellp3.setLayout(jPanellp3Layout);
        jPanellp3Layout.setHorizontalGroup(
            jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp3Layout.createSequentialGroup()
                .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanellp3Layout.createSequentialGroup()
                                    .addComponent(jLabel36)
                                    .addGap(10, 10, 10)
                                    .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(clients_cbproduct, 0, 180, Short.MAX_VALUE)
                                        .addComponent(clients_cbname, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jToggleButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel35)
                                .addGap(18, 18, 18)
                                .addComponent(clients_total)
                                .addGap(232, 232, 232))
                            .addGroup(jPanellp3Layout.createSequentialGroup()
                                .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanellp3Layout.createSequentialGroup()
                                        .addComponent(jToggleButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jToggleButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jToggleButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanellp3Layout.createSequentialGroup()
                                        .addComponent(jLabel38)
                                        .addGap(27, 27, 27)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanellp3Layout.createSequentialGroup()
                        .addGap(270, 270, 270)
                        .addComponent(jLabel40)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanellp3Layout.setVerticalGroup(
            jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp3Layout.createSequentialGroup()
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp3Layout.createSequentialGroup()
                                .addGap(169, 169, 169)
                                .addComponent(jLabel38))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp3Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanellp3Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(clients_cbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel36)
                            .addComponent(clients_cbproduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(71, 71, 71)
                .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clients_total)
                    .addComponent(jLabel35))
                .addGap(73, 73, 73)
                .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );

        jTabbedPane1.addTab("Crear Ordenes", jPanellp3);

        jPanellp2.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Orden#", "Productos", "Costo", "Estado"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel33.setText("Ordenes");

        jToggleButton6.setText("Eliminar");
        jToggleButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton6MouseClicked(evt);
            }
        });

        jToggleButton7.setText("Entregada/No entregada");
        jToggleButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton7MouseClicked(evt);
            }
        });

        jToggleButton8.setText("Mostrar Detalles");
        jToggleButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanellp2Layout = new javax.swing.GroupLayout(jPanellp2);
        jPanellp2.setLayout(jPanellp2Layout);
        jPanellp2Layout.setHorizontalGroup(
            jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp2Layout.createSequentialGroup()
                .addGroup(jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp2Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jLabel33))
                    .addGroup(jPanellp2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanellp2Layout.createSequentialGroup()
                                .addComponent(jToggleButton7)
                                .addGap(34, 34, 34)
                                .addComponent(jToggleButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jToggleButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 748, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanellp2Layout.setVerticalGroup(
            jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp2Layout.createSequentialGroup()
                .addGroup(jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp2Layout.createSequentialGroup()
                        .addGap(233, 233, 233)
                        .addComponent(jLabel31))
                    .addGroup(jPanellp2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addGroup(jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37))
        );

        jTabbedPane1.addTab("Ver Ordenes", jPanellp2);

        javax.swing.GroupLayout OrdersLayout = new javax.swing.GroupLayout(Orders.getContentPane());
        Orders.getContentPane().setLayout(OrdersLayout);
        OrdersLayout.setHorizontalGroup(
            OrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        OrdersLayout.setVerticalGroup(
            OrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane4MouseClicked(evt);
            }
        });

        jPanellp4.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel26.setText("Clientes");

        jLabel28.setText("Categoria:");

        jLabel29.setText("Nombre:");

        jLabel30.setText("Apellido:");

        jLabel42.setText("Descuento:");

        clientes_categoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Regular", "Frecuente", "Oro", "Platino" }));
        clientes_categoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientes_categoriaActionPerformed(evt);
            }
        });

        jToggleButton9.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton9.setText("Guardar");
        jToggleButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton9MouseClicked(evt);
            }
        });

        clientes_descuento.setText("5%");

        javax.swing.GroupLayout jPanellp4Layout = new javax.swing.GroupLayout(jPanellp4);
        jPanellp4.setLayout(jPanellp4Layout);
        jPanellp4Layout.setHorizontalGroup(
            jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp4Layout.createSequentialGroup()
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp4Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addGap(18, 18, 18)
                                .addComponent(clientes_descuento))
                            .addGroup(jPanellp4Layout.createSequentialGroup()
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel30)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(clientes_nombre)
                                    .addComponent(clientes_apellido)
                                    .addComponent(clientes_categoria, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32))
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addGap(209, 209, 209)
                        .addComponent(jToggleButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addGap(256, 256, 256)
                        .addComponent(jLabel26)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanellp4Layout.setVerticalGroup(
            jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp4Layout.createSequentialGroup()
                .addGap(285, 285, 285)
                .addComponent(jLabel32)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp4Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientes_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addGap(39, 39, 39)
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(clientes_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(clientes_categoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(clientes_descuento))
                .addGap(65, 65, 65)
                .addComponent(jToggleButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(183, 183, 183))
        );

        jTabbedPane4.addTab("Crear Clientes", jPanellp4);

        jPanellp8.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jToggleButton17.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton17.setText("Modificar");
        jToggleButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton17MouseClicked(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel47.setText("Clientes");

        jToggleButton21.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton21.setText("Eliminar");
        jToggleButton21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton21MouseClicked(evt);
            }
        });

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Apellido", "Categoria", "Descuento"
            }
        ));
        jScrollPane7.setViewportView(jTable7);

        javax.swing.GroupLayout jPanellp8Layout = new javax.swing.GroupLayout(jPanellp8);
        jPanellp8.setLayout(jPanellp8Layout);
        jPanellp8Layout.setHorizontalGroup(
            jPanellp8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp8Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanellp8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp8Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel54)
                        .addContainerGap(92, Short.MAX_VALUE))
                    .addGroup(jPanellp8Layout.createSequentialGroup()
                        .addComponent(jToggleButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToggleButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77))))
            .addGroup(jPanellp8Layout.createSequentialGroup()
                .addGap(248, 248, 248)
                .addComponent(jLabel47)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanellp8Layout.setVerticalGroup(
            jPanellp8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel54)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp8Layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addComponent(jLabel47)
                .addGap(28, 28, 28)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanellp8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
        );

        jTabbedPane4.addTab("Gestionar Clientes", jPanellp8);

        javax.swing.GroupLayout ClientesLayout = new javax.swing.GroupLayout(Clientes.getContentPane());
        Clientes.getContentPane().setLayout(ClientesLayout);
        ClientesLayout.setHorizontalGroup(
            ClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        ClientesLayout.setVerticalGroup(
            ClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4)
        );

        jPanellp6.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField10.setEditable(false);

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel44.setText("Ajustes");

        jLabel46.setText("Ruta de la base:");

        jToggleButton18.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton18.setText("Establecer nueva ruta");
        jToggleButton18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton18MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanellp6Layout = new javax.swing.GroupLayout(jPanellp6);
        jPanellp6.setLayout(jPanellp6Layout);
        jPanellp6Layout.setHorizontalGroup(
            jPanellp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp6Layout.createSequentialGroup()
                .addGroup(jPanellp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp6Layout.createSequentialGroup()
                        .addGroup(jPanellp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp6Layout.createSequentialGroup()
                                .addGap(144, 144, 144)
                                .addComponent(jToggleButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(jLabel49))
                            .addGroup(jPanellp6Layout.createSequentialGroup()
                                .addGap(177, 177, 177)
                                .addGroup(jPanellp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel46)
                                    .addComponent(jLabel44))))
                        .addGap(0, 62, Short.MAX_VALUE))
                    .addGroup(jPanellp6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField10)))
                .addContainerGap())
        );
        jPanellp6Layout.setVerticalGroup(
            jPanellp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp6Layout.createSequentialGroup()
                .addComponent(jLabel44)
                .addGap(39, 39, 39)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanellp6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel49)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanellp6Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jToggleButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(53, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout SettingsLayout = new javax.swing.GroupLayout(Settings.getContentPane());
        Settings.getContentPane().setLayout(SettingsLayout);
        SettingsLayout.setHorizontalGroup(
            SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanellp6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        SettingsLayout.setVerticalGroup(
            SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanellp6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(240, 211, 200));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/login(2).jpg"))); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/password.png"))); // NOI18N

        jPasswordField1.setBorder(null);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField4.setBorder(null);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elysian/images/user(1).png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Contraseña");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel6.setText("Inicia sesión");

        jRadioButton1.setBackground(new java.awt.Color(255, 255, 255));
        jRadioButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jRadioButton1.setLabel("   Recuérdame");

        jButton1.setBackground(new java.awt.Color(240, 211, 200));
        jButton1.setText("Iniciar Sesión");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(240, 211, 200));
        jLabel7.setText("¿Has olvidado tu contraseña?");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel8.setText("Usuario");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(102, 102, 102)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jRadioButton1)
                            .addGap(99, 99, 99)
                            .addComponent(jLabel7))
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(54, 54, 54)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel7))
                .addGap(31, 31, 31)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
    String email = jTextField4.getText();
    String password = new String(jPasswordField1.getPassword());

    if (email.equals("admin") && password.equals("admin")) {
        if (jRadioButton1.isSelected()) {
            // Recordar el usuario y la contraseña
            saveCredentials(email, password);
        } else {
            // Borrar los campos si no está seleccionado
            clearCredentials();
        }

        jToggleButton1.setVisible(true);
        jButton6.setVisible(true);
        Landingpage.pack();
        Landingpage.setLocationRelativeTo(this);
        Landingpage.setVisible(true);
        this.setVisible(false);
        Principal.addDialog(Landingpage);

    } else if (authenticate(email, password)) {
        if (jRadioButton1.isSelected()) {
            // Recordar el usuario y la contraseña
            saveCredentials(email, password);
        } else {
            // Borrar los campos si no está seleccionado
            clearCredentials();
        }

        jToggleButton1.setVisible(false);
        jButton6.setVisible(false);
        Landingpage.pack();
        Landingpage.setLocationRelativeTo(this);
        Landingpage.setVisible(true);
        this.setVisible(false);
        Principal.addDialog(Landingpage);

    } else {
        JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jToggleButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseClicked
        Users.pack();
        Users.setLocationRelativeTo(this);
        Users.setVisible(true);
        this.setVisible(false);
        Principal.addDialog(Users);
    }//GEN-LAST:event_jToggleButton1MouseClicked

    private void jToggleButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton2MouseClicked
        //userscombo_estado
        String nombre = users_name.getText();
        String apellido = users_apellido.getText();
        String correo = users_correo.getText();
        String contra = users_password.getText();
        String estado = userscombo_estado.getSelectedItem().toString();
        
        insertUser(nombre, apellido, correo, contra, estado);
    }//GEN-LAST:event_jToggleButton2MouseClicked

    private void jToggleButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton4MouseClicked
        // Guardar Producto
        String nombre = product_name.getText();
        String temp = product_price.getValue().toString();
        String categoria = product_categoria.getSelectedItem().toString();
        String material = product_material.getSelectedItem().toString();
        
        insertProduct(nombre, temp, categoria, material);
    }//GEN-LAST:event_jToggleButton4MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        Inventory.pack();
        Inventory.setLocationRelativeTo(this);
        Inventory.setVisible(true);
        this.setVisible(false);
        Principal.addDialog(Inventory);
    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        Orders.pack();
        Orders.setLocationRelativeTo(this);
        Orders.setVisible(true);
        this.setVisible(false);
        Principal.addDialog(Orders);
        
        //clients_cbname nombre (cargar combo box con todos los nombres de la tabla CLIENTES)
        //clients_cbproduc productos (cargar combo box con todos los productos de la tabla PRODUCTOS)
        // Cargar nombres de clientes en el combo box
        loadClientNames();

        // Cargar nombres de productos en el combo box
        loadProductNames();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jToggleButton9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton9MouseClicked
        String nombre = clientes_nombre.getText();
        String apellido = clientes_apellido.getText();
        String categoria = clientes_categoria.getSelectedItem().toString();
        int descuento = 0;
        String descuentoTexto = "";

        if (categoria.equals("Regular")) {
            descuento = 5;
            descuentoTexto = "5%";
        } else if (categoria.equals("Frecuente")) {
            descuento = 10;
            descuentoTexto = "10%";
        } else if (categoria.equals("Oro")) {
            descuento = 15;
            descuentoTexto = "15%";
        } else if (categoria.equals("Platino")) {
            descuento = 20;
            descuentoTexto = "20%";
        }

        clientes_descuento.setText(descuentoTexto); // Asegúrate de que clientes_descuento es accesible

        // Llama al método para insertar el cliente en la base de datos
        insertClient(nombre, apellido, categoria, descuento);
    }//GEN-LAST:event_jToggleButton9MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        Clientes.pack();
        Clientes.setLocationRelativeTo(this);
        Clientes.setVisible(true);
        this.setVisible(false);
        Principal.addDialog(Clientes);
        
        
        
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
        // TODO add your handling code here:
        Principal.closeAllDialogs();
        this.setVisible(true); 
        JOptionPane.showMessageDialog(this, "¡Has cerrado sesión exitosamente!", "Cerrar sesión", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton5MouseClicked

    private void jToggleButton15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton15MouseClicked
        saveChanges();
    }//GEN-LAST:event_jToggleButton15MouseClicked

    private void borrar_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_borrar_tableMouseClicked
    int selectedRow = jTable5.getSelectedRow();
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    int id = (int) jTable5.getValueAt(selectedRow, 0); // Asumiendo que la columna 0 es el ID
    
    // Llama al método para eliminar el usuario de la base de datos
    deleteUser(id);
    
    // Actualiza la tabla después de eliminar el registro
    loadUserData();
    }//GEN-LAST:event_borrar_tableMouseClicked

    private void jButton6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseClicked
        Settings.pack();
        Settings.setLocationRelativeTo(this);
        Settings.setVisible(true);
        this.setVisible(false);
        Principal.addDialog(Settings);
    }//GEN-LAST:event_jButton6MouseClicked

    private void jToggleButton18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton18MouseClicked
        // TODO add your handling code here:
        jTextField10.setText(DB_URL);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String dbPath = selectedFile.getAbsolutePath();
            updateDatabasePath(dbPath);
        }
    }//GEN-LAST:event_jToggleButton18MouseClicked

    private void jTabbedPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MouseClicked
        // TODO add your handling code here:
        loadUserData();
    }//GEN-LAST:event_jTabbedPane2MouseClicked

    private void jToggleButton16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton16MouseClicked
        // Modificar productos
        saveProductChanges();
    }//GEN-LAST:event_jToggleButton16MouseClicked

    private void jToggleButton19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton19MouseClicked
        // TODO add your handling code here:
    int selectedRow = jTable6.getSelectedRow();
    
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) jTable6.getValueAt(selectedRow, 0); // Asumiendo que la columna 0 es el ID

        // Llama al método para eliminar el producto de la base de datos
        deleteProduct(id);

        // Actualiza la tabla después de eliminar el registro
        loadProductData();
    }//GEN-LAST:event_jToggleButton19MouseClicked

    private void jTabbedPane3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane3MouseClicked
        loadProductData();
    }//GEN-LAST:event_jTabbedPane3MouseClicked

    private void jToggleButton17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton17MouseClicked
          saveClientChanges();
    }//GEN-LAST:event_jToggleButton17MouseClicked

    private void jToggleButton21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton21MouseClicked
        int selectedRow = jTable7.getSelectedRow();
    
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) jTable7.getValueAt(selectedRow, 0); // Asumiendo que la columna 0 es el ID

        // Llama al método para eliminar el cliente de la base de datos
        deleteClient(id);

        // Actualiza la tabla después de eliminar el registro
        loadClientData();
    }//GEN-LAST:event_jToggleButton21MouseClicked

    private void clientes_categoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientes_categoriaActionPerformed
        // TODO add your handling code here:
        String categoria = clientes_categoria.getSelectedItem().toString();
        int descuento = 0;
        String descuentoTexto = "";

        if (categoria.equals("Regular")) {
            descuento = 5;
            descuentoTexto = "5%";
        } else if (categoria.equals("Frecuente")) {
            descuento = 10;
            descuentoTexto = "10%";
        } else if (categoria.equals("Oro")) {
            descuento = 15;
            descuentoTexto = "15%";
        } else if (categoria.equals("Platino")) {
            descuento = 20;
            descuentoTexto = "20%";
        }
        
        clientes_descuento.setText(descuentoTexto);
    }//GEN-LAST:event_clientes_categoriaActionPerformed

    private void jTabbedPane4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane4MouseClicked
         loadClientData();
    }//GEN-LAST:event_jTabbedPane4MouseClicked

    private void jToggleButton13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton13MouseClicked
        // TODO add your handling code here:
        deleteSelectedRow();
        updateTotal();
    }//GEN-LAST:event_jToggleButton13MouseClicked

    private void jToggleButton12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton12MouseClicked
        // TODO add your handling code here:
         try {
            generateReceipt();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar el recibo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButton12MouseClicked

    private void jToggleButton11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton11MouseClicked
        // jTable3
         saveNewOrder();
    }//GEN-LAST:event_jToggleButton11MouseClicked

    private void jToggleButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton10MouseClicked
            addItemToTable();
            updateTotal();
    }//GEN-LAST:event_jToggleButton10MouseClicked

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        // TODO add your handling code here:
        loadOrderData();
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void jToggleButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton8MouseClicked
        // TODO add your handling code here:
        showSelectedOrderDetails();
    }//GEN-LAST:event_jToggleButton8MouseClicked

    private void jToggleButton6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton6MouseClicked
        deleteSelectedOrder();
    }//GEN-LAST:event_jToggleButton6MouseClicked

    private void jToggleButton7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton7MouseClicked
        toggleOrderStatus();
    }//GEN-LAST:event_jToggleButton7MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        showContactAdminMessage();
    }//GEN-LAST:event_jLabel7MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog Clientes;
    private javax.swing.JDialog Inventory;
    private javax.swing.JDialog Landingpage;
    private javax.swing.JDialog Orders;
    private javax.swing.JDialog Settings;
    private javax.swing.JDialog Users;
    private javax.swing.JToggleButton borrar_table;
    private javax.swing.JTextField clientes_apellido;
    private javax.swing.JComboBox<String> clientes_categoria;
    private javax.swing.JLabel clientes_descuento;
    private javax.swing.JTextField clientes_nombre;
    private javax.swing.JComboBox<String> clients_cbname;
    private javax.swing.JComboBox<String> clients_cbproduct;
    private javax.swing.JLabel clients_total;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanellp;
    private javax.swing.JPanel jPanellp1;
    private javax.swing.JPanel jPanellp2;
    private javax.swing.JPanel jPanellp3;
    private javax.swing.JPanel jPanellp4;
    private javax.swing.JPanel jPanellp5;
    private javax.swing.JPanel jPanellp6;
    private javax.swing.JPanel jPanellp7;
    private javax.swing.JPanel jPanellp8;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton10;
    private javax.swing.JToggleButton jToggleButton11;
    private javax.swing.JToggleButton jToggleButton12;
    private javax.swing.JToggleButton jToggleButton13;
    private javax.swing.JToggleButton jToggleButton15;
    private javax.swing.JToggleButton jToggleButton16;
    private javax.swing.JToggleButton jToggleButton17;
    private javax.swing.JToggleButton jToggleButton18;
    private javax.swing.JToggleButton jToggleButton19;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton21;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JToggleButton jToggleButton6;
    private javax.swing.JToggleButton jToggleButton7;
    private javax.swing.JToggleButton jToggleButton8;
    private javax.swing.JToggleButton jToggleButton9;
    private javax.swing.JComboBox<String> product_categoria;
    private javax.swing.JComboBox<String> product_material;
    private javax.swing.JTextField product_name;
    private javax.swing.JSpinner product_price;
    private javax.swing.JTextField users_apellido;
    private javax.swing.JTextField users_correo;
    private javax.swing.JTextField users_name;
    private javax.swing.JPasswordField users_password;
    private javax.swing.JComboBox<String> userscombo_estado;
    // End of variables declaration//GEN-END:variables
}
