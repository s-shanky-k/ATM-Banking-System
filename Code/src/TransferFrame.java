import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class TransferFrame extends JFrame {

    JLabel beneficiaryCardNo = new JLabel("Beneficiary Card No.");
    JTextField beneficiaryTf = new JTextField();
    JLabel amount = new JLabel("Amount (INR)");
    JTextField amountTf = new JTextField();
    JPanel jp = new JPanel();
    JButton transferBtn = new JButton();
    JButton cancelBtn = new JButton();
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

    TransferFrame(String no, Connection c) {
        setTitle("Transfer Funds");
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
        beneficiaryCardNo.setBounds(140, 290, 100, 30);
        beneficiaryCardNo.setOpaque(false);
        beneficiaryCardNo.setForeground(Color.white);
        beneficiaryTf.setBounds(250, 290, 150, 30);
        amount.setBounds(140, 340, 100, 30);
        amount.setOpaque(false);
        amount.setForeground(Color.white);
        amountTf.setBounds(250, 340, 100, 30);
        
        transferBtn=createSimpleButton("TRANSFER");
        transferBtn.setBounds(140, 410, 100, 30);
        cancelBtn=createSimpleButton("CANCEL");
        cancelBtn.setBounds(270, 410, 100, 30);
        background.setBounds(0, 0, this.getWidth(), this.getHeight());
    }

    public void addComponents() {
        jp.add(beneficiaryCardNo);
        jp.add(beneficiaryTf);
        jp.add(amount);
        jp.add(amountTf);
        jp.add(transferBtn);
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
    boolean validateCardNo(String bCardNo) {

        try {
            Statement stmt = con.createStatement();
            //step4 execute query  
            ResultSet rs = stmt.executeQuery("select card_no from atm");
            while (rs.next()) {
                if (rs.getString("card_no").equals(bCardNo)) {
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    boolean transferSQL(int amount, String bCardNo) {
        try {

            Statement stmt = con.createStatement();
            int SourceAmountDb, DestAmountDb = 0;
            String query = "insert into transfer values(?,?,?,to_date(sysdate,'dd-mm-yyy'),to_char(sysdate,'hh24:mi:ss'),?)";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs;

            rs = stmt.executeQuery("select balance from atm where card_no = " + bCardNo);
            while (rs.next()) {
                DestAmountDb = rs.getInt("balance");
            }

            rs = stmt.executeQuery("select balance from atm where card_no = " + card_no);
            while (rs.next()) {
                SourceAmountDb = rs.getInt("balance");
                if (SourceAmountDb > amount) {
                    SourceAmountDb = SourceAmountDb - amount;
                    DestAmountDb = DestAmountDb + amount;
                    stmt.executeUpdate("Update atm set balance =" + SourceAmountDb + "where card_no = " + card_no);
                    stmt.executeUpdate("Update atm set balance =" + DestAmountDb + "where card_no = " + bCardNo);
                    ps.setString(1, card_no);
                    ps.setString(2, bCardNo);
                    ps.setInt(3, amount);
                    ps.setInt(4, SourceAmountDb);
                    ps.executeUpdate();
                    con.commit();
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public void addActionListener() {
        transferBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int amt = Integer.parseInt(amountTf.getText());
                String bCardNo = beneficiaryTf.getText();

                //Small confirmation window
                final JLabel label = new JLabel();
                int result = JOptionPane.showConfirmDialog(new JFrame(), "Transaction of INR " + amountTf.getText() + " To Card Number " + beneficiaryTf.getText(), "Confirm Transaction",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    label.setText("You selected: Yes");
                    if (validateCardNo(bCardNo)) {
                        if (transferSQL(amt, bCardNo)) {
                            JOptionPane.showMessageDialog(new JFrame(), "\tCash Tranfered\n Thank You for Banking with Us!");
                            dispose();
                            NavigationFrame navigationFrame = new NavigationFrame(card_no, con);
                            navigationFrame.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(new JFrame(), "Insufficiant Bank Balance");
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Invalid Beneficiary Card Number");
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    label.setText("You selected: No");
                } else {
                    label.setText("None selected");
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
        beneficiaryTf.addFocusListener(new FocusListener() {	
			@Override
			public void focusLost(FocusEvent e) {
				if(beneficiaryTf.getText().length() == 0) {
					beneficiaryTf.setForeground(Color.gray);
					beneficiaryTf.setText("Enter Beneficiary");
					beneficiaryTf.setBorder(compoundBorder);
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(beneficiaryTf.getText().equals("Enter Beneficiary"))
					beneficiaryTf.setText("");
				beneficiaryTf.setForeground(Color.black);	
				beneficiaryTf.setBorder(compoundBorderAfterClick);
			}
		});
        
    }
            
    private void addMouseListener() {
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
