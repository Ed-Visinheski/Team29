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
    private JTextField txtOrderSearch;
    private JLabel jdOrderNumber;
    private JLabel jsStatus;
    private JLabel jsDishNumber;
    private JTextField txtStatus;
    private JTextField txtDishNumber;
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Orders");
        frame.setContentPane(new Orders().OrderPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    Connection con;
    PreparedStatement pst;
    public void connect()
    {
        String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29";
        String user = "in2033t29_d";
        String password = "m8mHWvcTuXA";

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

                String ordN , dishid,status , avail;
                ordN= txtOrderNumber.getText();
                status = txtStatus.getText();
                avail = txtDishNumber.getText();
                try{
                    pst = con.prepareStatement("insert into OrderClass(orderID,dishId,orderStatus)values (?,?,?)");
                    pst.setString(1,ordN);
                    pst.setString(2,avail);
                    pst.setString(3,status);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "record added!");
                    //table_load();
                    txtOrderNumber.setText("");
                    txtStatus.setText("");
                    txtDishNumber.setText("");
                    txtOrderNumber.requestFocus();
                }
                catch (SQLException e1){e1.printStackTrace();}

            }
        });
    }
}
