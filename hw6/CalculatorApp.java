package hw6;
import javax.swing.JFrame;

public class CalculatorApp {

    public static void main(String args[]){

        //create a JFrame titled “Calculator” and add a Calculator panel to it
        JFrame frame = new JFrame("Calculator");
        Calculator CPU = new Calculator();
        frame.add(CPU);
        
        //set the frame's size, component sizes, location, and visibility
        frame.setSize(200,300);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        //exit on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

    }
}
