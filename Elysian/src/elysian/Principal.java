/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elysian;


//BDD
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

/**
 *
 * @author j0c3lwiz
 */
public class Principal extends javax.swing.JFrame {


    private static String DB_URL = "jdbc:ucanaccess://C:\\Users\\j0c3lwiz\\Desktop\\Elysian.accdb";
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
    // Lista estática para almacenar los diálogos abiertos
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
        jPanellp1 = new javax.swing.JPanel();
        jToggleButton3 = new javax.swing.JToggleButton();
        jTextField5 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jToggleButton4 = new javax.swing.JToggleButton();
        jToggleButton5 = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Orders = new javax.swing.JDialog();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanellp3 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();
        jComboBox8 = new javax.swing.JComboBox<>();
        jToggleButton10 = new javax.swing.JToggleButton();
        jToggleButton11 = new javax.swing.JToggleButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jToggleButton12 = new javax.swing.JToggleButton();
        jComboBox9 = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
        jToggleButton13 = new javax.swing.JToggleButton();
        jLabel41 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox<>();
        jPanellp2 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        jToggleButton6 = new javax.swing.JToggleButton();
        jToggleButton7 = new javax.swing.JToggleButton();
        Clientes = new javax.swing.JDialog();
        jPanellp4 = new javax.swing.JPanel();
        jToggleButton8 = new javax.swing.JToggleButton();
        jTextField6 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jToggleButton9 = new javax.swing.JToggleButton();
        jToggleButton14 = new javax.swing.JToggleButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel43 = new javax.swing.JLabel();
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
                .addContainerGap(244, Short.MAX_VALUE))
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
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addGap(195, 195, 195))
        );

        javax.swing.GroupLayout LandingpageLayout = new javax.swing.GroupLayout(Landingpage.getContentPane());
        Landingpage.getContentPane().setLayout(LandingpageLayout);
        LandingpageLayout.setHorizontalGroup(
            LandingpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        LandingpageLayout.setVerticalGroup(
            LandingpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
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

        jTabbedPane2.addTab("Manejo de usaurios", jPanellp5);

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

        jPanellp1.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jToggleButton3.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton3.setText("Modificar");
        jToggleButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton3MouseClicked(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel21.setText("Inventario");

        jLabel22.setText("Categoria:");

        jLabel23.setText("Producto:");

        jLabel24.setText("Precio:");

        jLabel27.setText("Material:");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aritos", "Pulsera", "Cadena" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Oro", "Plata", "Acero" }));

        jToggleButton4.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton4.setText("Guardar");
        jToggleButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton4MouseClicked(evt);
            }
        });

        jToggleButton5.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton5.setText("Eliminar");
        jToggleButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton5MouseClicked(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Precio", "Categoria", "Material"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanellp1Layout = new javax.swing.GroupLayout(jPanellp1);
        jPanellp1.setLayout(jPanellp1Layout);
        jPanellp1Layout.setHorizontalGroup(
            jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp1Layout.createSequentialGroup()
                        .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp1Layout.createSequentialGroup()
                                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanellp1Layout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanellp1Layout.createSequentialGroup()
                                        .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel23)
                                            .addComponent(jLabel24)
                                            .addComponent(jLabel22))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField7)
                                            .addComponent(jTextField5)
                                            .addComponent(jComboBox2, 0, 178, Short.MAX_VALUE))))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel25))
                            .addGroup(jPanellp1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jToggleButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                        .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp1Layout.createSequentialGroup()
                                .addComponent(jToggleButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58)
                                .addComponent(jToggleButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29))
                    .addGroup(jPanellp1Layout.createSequentialGroup()
                        .addGap(275, 275, 275)
                        .addComponent(jLabel21)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanellp1Layout.setVerticalGroup(
            jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp1Layout.createSequentialGroup()
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp1Layout.createSequentialGroup()
                        .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp1Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jLabel21)
                                .addGap(201, 201, 201)
                                .addComponent(jLabel25))
                            .addGroup(jPanellp1Layout.createSequentialGroup()
                                .addGap(185, 185, 185)
                                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23))
                                .addGap(39, 39, 39)
                                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel24)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)
                                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel22)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(29, 29, 29)
                                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel27)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanellp1Layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanellp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jToggleButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(58, 58, 58))
        );

        javax.swing.GroupLayout InventoryLayout = new javax.swing.GroupLayout(Inventory.getContentPane());
        Inventory.getContentPane().setLayout(InventoryLayout);
        InventoryLayout.setHorizontalGroup(
            InventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanellp1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        InventoryLayout.setVerticalGroup(
            InventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanellp1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanellp3.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel34.setText("Total:");

        jLabel35.setText("Categoria:");

        jLabel36.setText("Producto:");

        jLabel37.setText("Precio:");

        jTextField9.setEditable(false);

        jLabel39.setText("Material:");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aritos", "Pulsera", "Cadena" }));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Oro", "Plata", "Acero" }));

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
                "Producto", "Precio", "Categoria", "Material"
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

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cadena de oro", "Cadena de plata" }));

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

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jorge Gonzales" }));

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
                                .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanellp3Layout.createSequentialGroup()
                                        .addComponent(jLabel39)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanellp3Layout.createSequentialGroup()
                                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel36)
                                            .addComponent(jLabel37)
                                            .addComponent(jLabel35))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField9)
                                            .addComponent(jComboBox7, 0, 180, Short.MAX_VALUE)
                                            .addComponent(jComboBox9, 0, 180, Short.MAX_VALUE)
                                            .addComponent(jComboBox10, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addComponent(jToggleButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
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
                                .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel34)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addComponent(jLabel34)
                        .addGap(26, 26, 26))
                    .addGroup(jPanellp3Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel36)
                            .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanellp3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39)
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                "Orden#", "Costo", "Estado"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel33.setText("Ordenes");

        jToggleButton6.setText("Eliminar");

        jToggleButton7.setText("Entregada/No entregada");

        javax.swing.GroupLayout jPanellp2Layout = new javax.swing.GroupLayout(jPanellp2);
        jPanellp2.setLayout(jPanellp2Layout);
        jPanellp2Layout.setHorizontalGroup(
            jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanellp2Layout.createSequentialGroup()
                .addGroup(jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 748, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31))
                    .addGroup(jPanellp2Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jLabel33)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jToggleButton7)
                .addGap(87, 87, 87)
                .addComponent(jToggleButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(133, 133, 133))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(jPanellp2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jPanellp4.setBackground(new java.awt.Color(240, 211, 200));
        jPanellp4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jToggleButton8.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton8.setText("Modificar");
        jToggleButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton8MouseClicked(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel26.setText("Clientes");

        jLabel28.setText("Categoria:");

        jLabel29.setText("Nombre:");

        jLabel30.setText("Apellido:");

        jLabel42.setText("Descuento:");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Regular", "Frecuente" }));

        jToggleButton9.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton9.setText("Guardar");
        jToggleButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton9MouseClicked(evt);
            }
        });

        jToggleButton14.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton14.setText("Eliminar");
        jToggleButton14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton14MouseClicked(evt);
            }
        });

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Apellido", "Categoria", "Descuento"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        jLabel43.setText("10%");

        javax.swing.GroupLayout jPanellp4Layout = new javax.swing.GroupLayout(jPanellp4);
        jPanellp4.setLayout(jPanellp4Layout);
        jPanellp4Layout.setHorizontalGroup(
            jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp4Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel43))
                            .addGroup(jPanellp4Layout.createSequentialGroup()
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel30)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField8)
                                    .addComponent(jTextField6)
                                    .addComponent(jComboBox4, 0, 178, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel32))
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jToggleButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addComponent(jToggleButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addComponent(jToggleButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );
        jPanellp4Layout.setVerticalGroup(
            jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanellp4Layout.createSequentialGroup()
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanellp4Layout.createSequentialGroup()
                                .addGap(285, 285, 285)
                                .addComponent(jLabel32))
                            .addGroup(jPanellp4Layout.createSequentialGroup()
                                .addGap(185, 185, 185)
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29))
                                .addGap(39, 39, 39)
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel30)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel28)
                                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel42)
                                    .addComponent(jLabel43))))
                        .addGap(0, 248, Short.MAX_VALUE))
                    .addGroup(jPanellp4Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel26)
                        .addGap(101, 101, 101)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanellp4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jToggleButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(58, 58, 58))
        );

        javax.swing.GroupLayout ClientesLayout = new javax.swing.GroupLayout(Clientes.getContentPane());
        Clientes.getContentPane().setLayout(ClientesLayout);
        ClientesLayout.setHorizontalGroup(
            ClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanellp4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ClientesLayout.setVerticalGroup(
            ClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanellp4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    if (email.equals("admin")&&password.equals("admin")) {
            jToggleButton1.setVisible(true);
            jButton6.setVisible(true);
            Landingpage.pack();
            Landingpage.setLocationRelativeTo(this);
            Landingpage.setVisible(true);
            this.setVisible(false);
            Principal.addDialog(Landingpage);
    }else if (authenticate(email, password)) {
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

    private void jToggleButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton3MouseClicked
       
    }//GEN-LAST:event_jToggleButton3MouseClicked

    private void jToggleButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton4MouseClicked

    private void jToggleButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton5MouseClicked

    private void jToggleButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton10MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton10MouseClicked

    private void jToggleButton11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton11MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton11MouseClicked

    private void jToggleButton12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton12MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton12MouseClicked

    private void jToggleButton13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton13MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton13MouseClicked

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
    }//GEN-LAST:event_jButton2MouseClicked

    private void jToggleButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton8MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton8MouseClicked

    private void jToggleButton9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton9MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton9MouseClicked

    private void jToggleButton14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton14MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton14MouseClicked

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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox9;
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
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton10;
    private javax.swing.JToggleButton jToggleButton11;
    private javax.swing.JToggleButton jToggleButton12;
    private javax.swing.JToggleButton jToggleButton13;
    private javax.swing.JToggleButton jToggleButton14;
    private javax.swing.JToggleButton jToggleButton15;
    private javax.swing.JToggleButton jToggleButton18;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JToggleButton jToggleButton5;
    private javax.swing.JToggleButton jToggleButton6;
    private javax.swing.JToggleButton jToggleButton7;
    private javax.swing.JToggleButton jToggleButton8;
    private javax.swing.JToggleButton jToggleButton9;
    private javax.swing.JTextField users_apellido;
    private javax.swing.JTextField users_correo;
    private javax.swing.JTextField users_name;
    private javax.swing.JPasswordField users_password;
    private javax.swing.JComboBox<String> userscombo_estado;
    // End of variables declaration//GEN-END:variables
}
