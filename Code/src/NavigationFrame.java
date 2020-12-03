import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class NavigationFrame extends JFrame {

    JButton transferBtn = new JButton("");
    JButton withdrawBtn = new JButton("");
    JButton depositBtn = new JButton("");
    JButton balanceBtn = new JButton("");
    JButton logOutBtn = new JButton("");
    JButton transBtn = new JButton("");
    JLabel welcomeLabel = new JLabel("", SwingConstants.CENTER);
    JPanel jp = new JPanel();
    JFrame frame1;
    JLabel background = new JLabel(new ImageIcon("2.jpg")) {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
        }
    };
    
    static JTable table;
    CompoundBorder compoundBorder;
    CompoundBorder compoundBorderAfterClick;
    String[] columnNames = {"Transferred from", "Transferred to", "amount", "date", "Time", "balance"};
    String from;
    String card_no;
    String Holder_name;

    Connection con;

    public NavigationFrame(String no, Connection c) {
        card_no = no;
        con = c;
        setTitle("Services");
        setBounds(10, 10, 720, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getUser();

        setLayout();

        background.setLayout(new FlowLayout());
        setIconImage((new ImageIcon("5.jpg").getImage()));
        setLocationAndSize();
        addComponents();
        addActionListener();
        addMouseListener();
        
        //creating compound border for Text Field to specify left margin to the text
        Border lineBorder = BorderFactory.createLineBorder(Color.blue, 1);
        Border emptyBorder = new EmptyBorder(0, 10, 0, 0); //left margin for text
        compoundBorder = new CompoundBorder(lineBorder, emptyBorder);
        Border lineBorder1 = BorderFactory.createLineBorder(new Color(108, 99, 255), 3);
        Border emptyBorder1 = new EmptyBorder(0, 10, 0, 0); //left margin for text
        compoundBorderAfterClick = new CompoundBorder(lineBorder1, emptyBorder1);
        add(jp);

    }

    public void setLayout() {
        jp.setLayout(null);
    }

    public void setLocationAndSize() {

        welcomeLabel.setBounds(123, 230, 295, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 17));
        welcomeLabel.setForeground(Color.orange);
        welcomeLabel.setOpaque(true);
        welcomeLabel.setBackground(Color.black);
        
        withdrawBtn=createSimpleButton("WITHDRAW");
        withdrawBtn.setBounds(140, 300, 120, 30);
        
        transferBtn=createSimpleButton("TRANSFER");
        transferBtn.setBounds(270, 300, 120, 30);
        
        depositBtn= createSimpleButton("DEPOSIT");
        depositBtn.setBounds(140, 350, 120, 30);
        
        balanceBtn=createSimpleButton("BALANCE INQUIRY");
        balanceBtn.setBounds(270, 350, 120, 30);
        
        transBtn=createSimpleButton("TRANSFER HISTORY");
        transBtn.setBounds(140, 400, 120, 30);
        
        logOutBtn=createSimpleButton("LOG OUT");
        logOutBtn.setBounds(270, 400, 120, 30);
        logOutBtn.setOpaque(true);
        
        //logOutBtn.setBackground(Color.RED);
        background.setBounds(0, 0, this.getWidth(), this.getHeight());
        welcomeLabel.setText("Welcome " + Holder_name + " to CSK Banking!");

    }

    public void addComponents() {
        jp.add(welcomeLabel);
        jp.add(withdrawBtn);
        jp.add(transferBtn);
        jp.add(depositBtn);
        jp.add(balanceBtn);
        jp.add(transBtn);
        jp.add(logOutBtn);
        jp.add(background);

    }
    
     public JButton createSimpleButton(String text) {
		  JButton button = new JButton(text);
		  button.setForeground(Color.BLACK);
		  button.setBackground(Color.WHITE);
		  Border line = new LineBorder(Color.BLACK);
		  Border margin = new EmptyBorder(5, 15, 5, 15);
		  Border compound = new CompoundBorder(line, margin);
		  button.setBorder(compound);
		  return button;
     }

    public void balanceSQL() {
        try {

            Statement stmt = con.createStatement();

            //Execute query  
            ResultSet rs1 = stmt.executeQuery("select balance from atm where card_no = " + card_no);

            while (rs1.next()) {
                int amountDb = rs1.getInt("balance");
                JOptionPane.showMessageDialog(new JFrame(), "Bank Balance is INR " + amountDb);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void getUser() {

        try {
            Statement stmt = con.createStatement();

            //Execute query  
            ResultSet rs2 = stmt.executeQuery("select Holder_name from atm where card_no =" + card_no);

            while (rs2.next()) {
                Holder_name = rs2.getString("holder_name");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void showTableData() {
        frame1 = new JFrame("TRANSFER STATEMENT");

        frame1.setLayout(new BorderLayout());
        frame1.setIconImage((new ImageIcon("5.jpg").getImage()));
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setColumnIdentifiers(columnNames);
        table = new JTable();

        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setRowHeight(table.getRowHeight() + 10);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        from = card_no;
        String card = "";
        String name = "";
        int amount = 0;
        String date = "";
        String time = "";
        int balance = 0;
        try {
            PreparedStatement pst;
            pst = con.prepareStatement("select * from transfer where From_card=" + from);
            ResultSet rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                card = rs.getString("from_card");
                name = rs.getString("to_card");
                amount = rs.getInt("amount");
                date = rs.getString("transfer_date").substring(0, 10);
                System.out.println(date);
                time = rs.getString("transfer_time");
                balance = rs.getInt("balance");
                model.addRow(new Object[]{card, name, amount, date, time, balance});
                i++;
            }
            if (i < 1) {
                JOptionPane.showMessageDialog(null, "No Record Found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception ex) {

        }
        frame1.add(scroll);
        frame1.setVisible(true);
        frame1.setBackground(Color.GRAY);
        frame1.setBounds(500, 200, 600, 320);
    }

    public void addActionListener() {

        transferBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                TransferFrame transferFrame = new TransferFrame(card_no, con);
                transferFrame.setVisible(true);
                dispose();
            }
        });

        balanceBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //BALANCE FRAME OBJ
                balanceSQL();
            }
        });

        withdrawBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                WithdrawFrame withdrawFrame = new WithdrawFrame(card_no, con);
                withdrawFrame.setVisible(true);
                dispose();
            }
        });

        depositBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                DepositFrame depositFrame = new DepositFrame(card_no, con);
                depositFrame.setVisible(true);
                dispose();
            }
        });

        logOutBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(new JFrame(), "Logged Out");
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose();
                //Need to go back to previous page
            }
        });
        
        transBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTableData();
            }
        });
        
    }
    public void addMouseListener() {
        transferBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  transferBtn.setBackground(Color.orange);
             }
             @Override
             public void mouseExited(MouseEvent evt) {
                   transferBtn.setBackground(Color.WHITE);
             }
        });
        
        balanceBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  balanceBtn.setBackground(Color.orange);
             }
             @Override
             public void mouseExited(MouseEvent evt) {
                   balanceBtn.setBackground(Color.WHITE);
             }
        });
        
        withdrawBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  withdrawBtn.setBackground(Color.orange);
             }
             @Override
             public void mouseExited(MouseEvent evt) {
                   withdrawBtn.setBackground(Color.WHITE);
             }
        });
        
        depositBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  depositBtn.setBackground(Color.orange);
             }
             @Override
             public void mouseExited(MouseEvent evt) {
                   depositBtn.setBackground(Color.WHITE);
             }
        });
        
        logOutBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  logOutBtn.setBackground(Color.orange);
             }
             @Override
             public void mouseExited(MouseEvent evt) {
                   logOutBtn.setBackground(Color.WHITE);
             }
        });
        
        transBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  transBtn.setBackground(Color.orange);
             }
             @Override
             public void mouseExited(MouseEvent evt) {
                   transBtn.setBackground(Color.WHITE);
             }
        });
    
    }

}
