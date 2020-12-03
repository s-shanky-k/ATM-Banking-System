import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class DepositFrame extends JFrame {

    JLabel limit = new JLabel("Max Limit is 10000", SwingConstants.CENTER);
    JLabel amount = new JLabel("Amount", SwingConstants.CENTER);
    JTextField amountTf = new JTextField();
    JButton depositBtn = new JButton();
    JButton cancelBtn = new JButton();
    JPanel jp = new JPanel();
    JLabel background = new JLabel(new ImageIcon("2.jpg")) {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
        }
    };
    
    CompoundBorder compoundBorder;
    CompoundBorder compoundBorderAfterClick;
    String card_no;
    Connection con;

    DepositFrame(String no, Connection c) {
        setTitle("Deposit");
        setBounds(10, 10, 720, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout();
        background.setLayout(new FlowLayout());

        setIconImage((new ImageIcon("5.jpg").getImage()));
        setLocationAndSize();
        addComponents();
        addActionListener();
        addFocusListener();
        addMouseListener();
        
        //creating compound border for Text Field to specify left margin to the text
        Border lineBorder = BorderFactory.createLineBorder(Color.blue, 1);
        Border emptyBorder = new EmptyBorder(0, 10, 0, 0); //left margin for text
        compoundBorder = new CompoundBorder(lineBorder, emptyBorder);
        Border lineBorder1 = BorderFactory.createLineBorder(new Color(108, 99, 255), 3);
        Border emptyBorder1 = new EmptyBorder(0, 10, 0, 0); //left margin for text
        compoundBorderAfterClick = new CompoundBorder(lineBorder1, emptyBorder1);
        add(jp);
        card_no = no;
        con = c;
    }

    public void setLayout() {
        jp.setLayout(null);
    }

    public void setLocationAndSize() {
        limit.setFont(new Font("Arial", Font.BOLD, 20));
        limit.setForeground(Color.orange);
        limit.setBackground(Color.black);
        limit.setBounds(123, 290, 294, 30);
        amount.setOpaque(false);
        amount.setBounds(140, 350, 100, 30);
        amount.setForeground(Color.white);
        amountTf.setBounds(250, 350, 100, 30);
        depositBtn=createSimpleButton("DEPOSIT");
        depositBtn.setBounds(140, 410, 100, 30);
        cancelBtn=createSimpleButton("CANCEL");
        cancelBtn.setBounds(270, 410, 100, 30);
        background.setBounds(0, 0, this.getWidth(), this.getHeight());
    }

    public void addComponents() {
        jp.add(limit);
        jp.add(amount);
        jp.add(amountTf);
        jp.add(depositBtn);
        jp.add(cancelBtn);
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
    void depositSQL(int amount) {

        try {

            Statement stmt = con.createStatement();

            //Execute query  
            ResultSet rs1 = stmt.executeQuery("select balance from atm where card_no = " + card_no);

            while (rs1.next()) {
                int amountDb = rs1.getInt("balance");
                amountDb = amountDb + amount;
                stmt.executeUpdate("Update atm set balance =" + amountDb + "where card_no = " + card_no);
                con.commit();
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void addActionListener() {
        depositBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int amt = Integer.parseInt(amountTf.getText());
                if (amt > 10000) {
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Deposit Limit Exceeded",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    //Small confirmation window
                    final JLabel label = new JLabel();
                    int result = JOptionPane.showConfirmDialog(new JFrame(), "Deposit of INR " + amountTf.getText(), "Confirm Deposit",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        label.setText("You selected: Yes");
                        depositSQL(amt);
                        JOptionPane.showMessageDialog(new JFrame(), "Cash Deposited \n Thank You for Banking with Us!");
                        dispose();
                        NavigationFrame navigationFrame = new NavigationFrame(card_no, con);
                        navigationFrame.setVisible(true);

                    } else if (result == JOptionPane.NO_OPTION) {
                        label.setText("You selected: No");
                    } else {
                        label.setText("None selected");
                    }

                    //NEED TO DO JDBC VALIDATION
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //To Display a message
                JOptionPane.showMessageDialog(new JFrame(), "Cancelled");
                dispose();
                NavigationFrame navigationFrame = new NavigationFrame(card_no, con);
                navigationFrame.setVisible(true);
                //Need to go back to previous page
            }
        });
    }
    
    public void addFocusListener() {
        amountTf.addFocusListener(new FocusListener() {	
			@Override
			public void focusLost(FocusEvent e) {
				if(amountTf.getText().length() == 0) {
					amountTf.setForeground(Color.gray);
					amountTf.setText("Enter Amount");
					amountTf.setBorder(compoundBorder);
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(amountTf.getText().equals("Enter Amount"))
					amountTf.setText("");
				amountTf.setForeground(Color.black);	
				amountTf.setBorder(compoundBorderAfterClick);
			}
		});
        
    }

    public void addMouseListener() {
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
        cancelBtn.addMouseListener(new MouseAdapter() {
                @Override
             public void mouseEntered(MouseEvent evt) {
                  cancelBtn.setBackground(Color.orange);
             }
                @Override
             public void mouseExited(MouseEvent evt) {
                   cancelBtn.setBackground(Color.WHITE);
             }
        });
        
    }

}
