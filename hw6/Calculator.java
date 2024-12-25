package hw6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.InterruptedException;

public class Calculator extends JPanel {

    //the expression display
    private String expression = "0"; //stores the math expression, initialized as 0
    JPanel display_panel; //contains a JTextArea to display the expression
    JTextArea mathTextArea; //stores the expression text

    //the numbers, dot, operators, clear, and delete buttons
    JPanel clear_del_panel; // contains the 'Clear' and 'Delete' buttons
    JPanel opr_panel; // contains the operator buttons
    JButton clearButton; // clear button
    JButton delButton; // delete button
    String operations[] = {"/","*","-","+"};
    JButton inputButtons[];

    JPanel numbers_panel; // contains the numbers, dot, and cpu buttons
    String numbers[] = {"7","8","9","4","5","6","1","2","3","0","."};
    JButton cpuButton; // cpu button
    JPanel input_panel; // contains the number and operator buttons
    
    /* Calculator(): default contructor
    Sets up the calculator UI with the CPU and number listerners.
    Each individual CPU does computation independently.
    */
    public Calculator(){

        //set up Calculator
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200,300));
        setFocusable(true);

        //set up display_panel with its text area
        display_panel = new JPanel(new BorderLayout());
        mathTextArea = new JTextArea("0");
        mathTextArea.setEditable(false);
        mathTextArea.setFont(new Font("Ariel", Font.PLAIN, 30));
        mathTextArea.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        display_panel.setPreferredSize(new Dimension(200,60));
        display_panel.setBackground(mathTextArea.getBackground());
        display_panel.add(mathTextArea, BorderLayout.EAST);

        // set up the 'Clear' and 'Del' buttons
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new NumberListener());
        delButton = new JButton("Del");
        delButton.addActionListener(new NumberListener());
        clear_del_panel = new JPanel();
        clear_del_panel.setLayout(new BorderLayout());
        clear_del_panel.add(clearButton, BorderLayout.WEST);
        clear_del_panel.add(delButton, BorderLayout.EAST);
        clear_del_panel.setPreferredSize(new Dimension(200,40));
        
        //initialize pointers for the operation buttons 
        inputButtons = new JButton[17];

        // set up the operator buttons
        opr_panel = new JPanel();
        //opr_panel.setLayout(new BoxLayout(opr_panel, BoxLayout.Y_AXIS));
        opr_panel.setLayout(new GridLayout(4,1,1,1));
        opr_panel.setPreferredSize(new Dimension(50,200));
        for(int i=0; i<operations.length;i++){
            inputButtons[i+10] = new JButton(operations[i]);
            inputButtons[i+10].addActionListener(new NumberListener());
            opr_panel.add(inputButtons[i+10]);
        }

        // set up the numbers, dot, and cpu buttons
        numbers_panel = new JPanel();
        numbers_panel.setLayout(new GridLayout(4,3,1,1));
        numbers_panel.setPreferredSize(new Dimension(150,200));

        // add number buttons with number listeners
        for(int i=0; i<numbers.length;i++){
            inputButtons[i] = new JButton(numbers[i]);
            inputButtons[i].addActionListener(new NumberListener());
            numbers_panel.add(inputButtons[i]);
        }

        // add cpu button
        cpuButton = new JButton("=");
        cpuButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        cpuButton.addActionListener(new CPUListener());
        numbers_panel.add(cpuButton);

        // set up the numbers and operator panels side by side
        input_panel = new JPanel();
        input_panel.setLayout(new BorderLayout());
        input_panel.add(opr_panel, BorderLayout.WEST);
        input_panel.add(numbers_panel, BorderLayout.EAST);
        input_panel.setPreferredSize(new Dimension(150,200));
        
        //add panels to Calculator
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(display_panel);
        add(clear_del_panel);
        add(input_panel);
    }

    /* NumberListener:
    A number, dot, or operation button adds a valid number, dot, or operation value to the expression display.
    Clear button makes expression display '0' and delete button removes one character in the display area.
    */
    private class NumberListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){

            //the button's text value
            String buttonVal = ((JButton) e.getSource()).getText();
            expression = mathTextArea.getText();

            // reset the display if last evaluation was undefined
            if(expression.equals("Infinity")){
                expression="";
            }

            switch (buttonVal){
                
                //to add dots
                case ".":
                    if(expression.equals("0")){//keep the 0 if the expression is just "0" 
                        expression = "0"+buttonVal;
                    }
                    else if(expression.charAt(expression.length()-1)=='.'){//do not allow multiple dots to be added
                        break;
                    }
                    else{
                        expression = expression + buttonVal;
                    }
                    break;

                //to clear display area
                case "Clear":
                    expression ="0";
                    break;

                //to delete one character
                case "Del":
                    if (expression.length()>0){//deletes one character at the end
                        expression = expression.substring(0,expression.length()-1);
                    }
                    if(expression.length()==0){//display '0' if the expression is fully deleted
                        expression="0";
                    }
                    break;
                
                default:
                    // replace the last character of the expression if needed

                    // the expression isn't a single digit and...
                    if(expression.length()>1){
                        // expression is some operator followed by "0"
                        if((expression.charAt(expression.length()-1)=='0' && isOperation(String.valueOf(expression.charAt(expression.length()-2))))
                        //the button pressed is an operator and the last value is an operator to be replaced
                        ||(isOperation(buttonVal) && isOperation(String.valueOf(expression.charAt(expression.length()-1)))))
                        {
                            expression = expression.substring(0,expression.length()-1);
                        }
                    }
                    
                    // the expression is '0' and the button value is a number
                    else if(expression.equals("0") && !isOperation(buttonVal)){
                        //expression = expression.substring(0,expression.length()-1);
                        expression = "";
                    }       

                    //add the number or operator
                    expression = expression + buttonVal;
                    break;
            }

            //update the display text area with the new expression
            mathTextArea.setText(expression);
        }
    }
    
    /*
    Create and run a Calculator Thread depending on which CPU button is pressed.
    Other CPU buttons can be pressed and the expression can be adjusted while a CPU is performing its computation.
    */
    private class CPUListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == cpuButton){
                CalculatorThread calc = new CalculatorThread();
                calc.start();
            }
        }   
    }

    /* CalculatorThread:
    Once pressed, CPU button is freezed for 5000 milliseconds while the JTextArea display the result.
    */
    private class CalculatorThread extends Thread{

        @Override
		public void run(){
            cpuButton.setEnabled(false);//disable the CPU button
            
            mathTextArea.setText(String.valueOf(evaluation(expression)));// evaluate the expression and display the result in textArea
            
            //put the thread to sleep for 5000 milliseconds
            try {
                CalculatorThread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cpuButton.setEnabled(true);// enable the CPU button
        }
    }
    
    /* isOperation(String source):
    Returns true if the string is a operator (+ - * /), otherwise, return false.
    */
    public boolean isOperation(String source){
        for(int i=0;i<4;i++){
            if(source.equals(operations[i])){
                return true;
            }
        }
        return false;
    }
    
    /*
    Code below for evaluation is given and unmodified.
    */

    private static double evaluation(String expression){
        String[] sums = expression.split("(?<=\\+)|(?=\\+)|(?<=\\-)|(?=\\-)");
			System.out.println(expression);

			int index=2;
			double ans=0;
			String sums0 = sums[0];
			
			if( sums0.equals("-") ) {
				ans = -evaluateProduct(sums[1]);
				index = 3;
			}
			else {
				ans = evaluateProduct(sums[0]);
			}
			
			for(int i=index; i<sums.length; i+=2) {
				double ans_level2 = evaluateProduct( sums[i] );
				
				switch(sums[i-1]) {				
				case "+":
					ans += ans_level2;
					break;
				case "-":
					ans -= ans_level2;
					break;
				default:
					System.out.println("do not recognize method");
				}
			}
            return ans;
        
    }

    private static double evaluateProduct(String expression0){
        String[] sums = expression0.split("(?<=\\+)|(?=\\+)|(?<=\\-)|(?=\\-)|(?<=\\*)|(?=\\*)|(?<=\\/)|(?=\\/)");

        int index=2;
        double ans=0;
        String sums0 = sums[0];
        
        if( sums0.equals("-") ) {
            ans = Double.parseDouble( sums[0] + sums[1] ) ;			
            index = 3;
        }
        else {
            ans = Double.parseDouble( sums[0]  ) ;				
        }
        
        for(int i=index; i<sums.length; i+=2) {
            double aNum = Double.parseDouble( sums[i] );
            switch(sums[i-1]) {				
            case "+":
                ans += aNum;
                break;
            case "-":
                ans -= aNum;
                break;
            case "*":
                ans *= aNum;
                break;
            case "/":
                ans /= aNum;
                break;
            default:				
            }
        }	
        return ans;				
    }

}
