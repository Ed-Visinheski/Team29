package SaharTicketOrders;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class Orders {
    private JPanel OrderPanel;
    private JLabel jsOrder;
    private JTextField txtOrderNumber;
    private JButton butSave;
    private JTable table_1;
    private JButton butUpdate;
    private JButton butDelete;
    private JButton butSearch;
    private JTextField txtId;
    private JLabel jdOrderNumber;
    private JLabel jsStatus;
    private JLabel jsDishNumber;
    private JTextField txtStatus;
    private JTextField txtDishNumber;
    private JLabel jsTableNumber;
    private JTextField txtTableNumber;
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    Connection con;
    PreparedStatement pst;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Orders");
        frame.setContentPane(new Orders().OrderPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void connect()
    {
        String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29";
        String user = "in2033t29_a";
        String password = "NvG2lCOEy_g";

        try {
            //Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
           // stmt = conn.createStatement();

            //con = DriverManager.getConnection("jdbc:mysql://localhost/in2033t29", "in2033t29_d","m8mHWvcTuXA");
            System.out.println("success");
        }
       // catch (ClassNotFoundException ex){}
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void table_load(){
        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM OrderClass");
            //rs.getString("orderID");
           // pst = con.prepareStatement("select * from OrderClass");
          //  ResultSet rs = pst.executeQuery();
            table_1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch(SQLException e3){e3.printStackTrace();}
    }

    public Orders() {
        connect();
        table_load();
        butSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String ordN ,status , avail , tabN;
                ordN= txtOrderNumber.getText();
                tabN = txtTableNumber.getText();
                status = txtStatus.getText();
                avail = txtDishNumber.getText();
                try{
                    pst = con.prepareStatement("insert into OrderClass(orderID,dishId,tableNumber,orderStatus)values (?,?,?,?)");
                    pst.setString(1,ordN);
                    pst.setString(2,avail);
                    pst.setString(3,tabN);
                    pst.setString(4,status);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "record added!");
                    //table_load();
                    txtOrderNumber.setText("");
                    txtDishNumber.setText("");
                    txtTableNumber.setText("");
                    txtStatus.setText("");
                    txtOrderNumber.requestFocus();
                }
                catch (SQLException e1){e1.printStackTrace();}

            }
        });
        butSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String empid = txtId.getText();
                    pst = con.prepareStatement("select orderID,dishID,tableNumber, orderStatus from OrderClass where orderID = ?");
                    pst.setString(1,empid);
                    ResultSet rs = pst.executeQuery();
                    if(rs.next() == true){
                        String nameO = rs.getString(1);
                        String nameD = rs.getString(2);
                        String nameT = rs.getString(3);
                        String nameS = rs.getString(4);
                        txtOrderNumber.setText(nameO);
                        txtDishNumber.setText(nameD);
                        txtTableNumber.setText(nameT);
                        txtStatus.setText(nameS);
                    }
                    else {
                        txtOrderNumber.setText("");
                        txtDishNumber.setText("");
                        txtTableNumber.setText("");
                        txtStatus.setText("");
                        JOptionPane.showMessageDialog(null, "Invalid OrderNumber");

                    }
                }catch(SQLException e5){e5.printStackTrace();}
            }
        });
        butUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ordN ,status , avail , tabN;
                ordN= txtOrderNumber.getText();
                tabN = txtTableNumber.getText();
                status = txtStatus.getText();
                avail = txtDishNumber.getText();
                try{
                    pst = con.prepareStatement("update OrderClass set orderID = ?,dishID = ? , tableNumber = ?, orderStatus = ? ");
                    pst.setString(1,ordN );
                    pst.setString(2,avail );
                    pst.setString(3,tabN);
                    pst.setString(4,status );
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Order Updated");
                    table_load();
                    txtOrderNumber.setText("");
                    txtDishNumber.setText("");
                    txtTableNumber.setText("");
                    txtStatus.setText("");

                }catch(SQLException e8){e8.printStackTrace();}
            }
        });
        butDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id;
                id = txtId.getText();
                try{
                    pst = con.prepareStatement("delete from OrderClass where orderID = ?");
                    pst.setString(1,id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "order deleted");
                    table_load();
                    txtOrderNumber.setText("");
                    txtDishNumber.setText("");
                    txtTableNumber.setText("");
                    txtStatus.setText("");

                }

                catch (SQLException e9){
                    e9.printStackTrace();
                }

            }
        });
    }
}
