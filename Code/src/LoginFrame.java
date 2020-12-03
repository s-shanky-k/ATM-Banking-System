import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.swing.border.*;

public class LoginFrame extends JFrame {

    JFrame jf = new JFrame();
    JLabel userLabel = new JLabel("CARD NUMBER", SwingConstants.CENTER);
    JLabel pinLabel = new JLabel("PIN", SwingConstants.CENTER);
    JTextField userTf = new JTextField();
    JPasswordField pinTf = new JPasswordField();
    JButton loginBtn = new JButton("");
    JButton resetBtn = new JButton("");
    Container frame = getContentPane();
    //BufferedImage myImage;
    CompoundBorder compoundBorder;
    CompoundBorder compoundBorderAfterClick;
    Connection con;
    
    //Setting Background
    JLabel background = new JLabel(new ImageIcon("2.jpg")) {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
        }
    };
    

    public LoginFrame() {

        setTitle("Login");
        setBounds(10, 10, 720, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout();

        background.setLayout(new FlowLayout());

        setIconImage((new ImageIcon("5.jpg").getImage()));
        setLocationAndSize();
        addComponents();
        addActionListener();

        //Creating compound border for Text Field to specify left margin to the text
        Border lineBorder = BorderFactory.createLineBorder(Color.blue, 1);
        Border emptyBorder = new EmptyBorder(0, 10, 0, 0); //left margin for text
        compoundBorder = new CompoundBorder(lineBorder, emptyBorder);
        Border lineBorder1 = BorderFactory.createLineBorder(new Color(108, 99, 255), 3);
        Border emptyBorder1 = new EmptyBorder(0, 10, 0, 0); //left margin for text
        compoundBorderAfterClick = new CompoundBorder(lineBorder1, emptyBorder1);

    }

    public void setLayout() {
        this.setLayout(new BorderLayout());
        frame.setLayout(null);
    }

    public void setLocationAndSize() {

        userLabel.setOpaque(false);
        userLabel.setBounds(140, 290, 100, 30);
        
        userLabel.setForeground(Color.white);
        userTf.setBounds(250, 290, 150, 30);

        pinLabel.setBounds(140, 340, 100, 30);
        pinLabel.setForeground(Color.white);
        
        pinTf.setBounds(250, 340, 100, 30);
        pinLabel.setOpaque(false);
        
        loginBtn=createSimpleButton("LOGIN");
        loginBtn.setBounds(140, 410, 100, 30);
        
        resetBtn=createSimpleButton("RESET");
        resetBtn.setBounds(270, 410, 100, 30);

        background.setBounds(0, 0, this.getWidth(), this.getHeight());

    }

    public void addComponents() {

        frame.add(userLabel);
        frame.add(userTf);
        frame.add(pinLabel);
        frame.add(pinTf);
        frame.add(loginBtn);
        frame.add(resetBtn);
        frame.add(background);

    }

    public void addActionListener() {
        loginBtn.addActionListener(new LoginListener());
        resetBtn.addActionListener(new ResetListener());
        userTf.addFocusListener(new FocusListener() {	
			@Override
			public void focusLost(FocusEvent e) {
				if(userTf.getText().length() == 0) {
					userTf.setForeground(Color.gray);
					userTf.setText("Enter Username");
					userTf.setBorder(compoundBorder);
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(userTf.getText().equals("Enter Username")) {
					userTf.setText("");
				}
				userTf.setForeground(Color.black);	
				userTf.setBorder(compoundBorderAfterClick);
			}
		});
        
        pinTf.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if(pinTf.getText().length() == 0) {
					pinTf.setForeground(Color.gray);
					pinTf.setEchoChar((char)0);
					pinTf.setText("Enter pin");
					pinTf.setBorder(compoundBorder);
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(pinTf.getText().equals("Enter pin"))
					pinTf.setText("");
				pinTf.setEchoChar('\u2022');
				pinTf.setForeground(Color.gray);
				pinTf.setBorder(compoundBorderAfterClick);
			}
		});
        
        loginBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  loginBtn.setBackground(Color.orange);
             }
                @Override
             public void mouseExited(MouseEvent evt) {
                   loginBtn.setBackground(Color.WHITE);
             }
        });
        
        resetBtn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent evt) {
                  resetBtn.setBackground(Color.orange);
             }
             @Override
             public void mouseExited(MouseEvent evt) {
                   resetBtn.setBackground(Color.WHITE);
             }
        });
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
  

    boolean validateCredentials(String card_no, String pin) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //step2 create  the connection object  
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/orcl", "atm_admin", "atm_admin");

            //step3 create the statement object  
            Statement stmt = con.createStatement();

            //step4 execute query  
            ResultSet rs = stmt.executeQuery("select card_no,pin from atm");

            while (rs.next()) {
                if (rs.getString("card_no").equals(card_no) && rs.getString("pin").equals(pin)) {
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    class LoginListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Login");
            String card_no = userTf.getText();
            String pin = pinTf.getText();
            System.out.println(card_no);
            System.out.println(pin);

            //NEED TO DO JDBC VALIDATION
            if (validateCredentials(card_no, pin)) {
                NavigationFrame navigationFrame = new NavigationFrame(card_no, con);
                navigationFrame.setVisible(true);
                dispose();
            } else {
                //To Display a message
                JOptionPane.showMessageDialog(new JFrame(), "Invalid Credentials");
            }
        }
    }

    class ResetListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Reset");
            userTf.setText("");
            pinTf.setText("");
        }
    }
}
    

