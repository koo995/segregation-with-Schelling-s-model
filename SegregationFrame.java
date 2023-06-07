import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class SegregationFrame extends JFrame { 
    private static final int DEFAULT_WIDTH = 700;
    private static final int DEFAULT_HEIGHT = 850;
    private int stepCount ; // 실행횟수를 세기위한 counter을 만듬
    private double tolerance = 0.25;
    private boolean diversity = false;
    //메인frame에 넣어줄 Panel 3개를 선언해 주었다.
    private JPanel northPanel;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JButton[][] smallPanelArr; //하우스들에 해당하는 smallPanel을 담을 배열을 선언한다. smallPanel들은 northPanel에 넣을 예정
    private Segregation s; //segregation객체를 담아줄 변수 S을 선언한다.

    //TextArea에서 값을 변경해줄 변수들을 먼저 객체를 만들어 초기화 해주었다.
    private JTextArea size = new JTextArea();
    private JTextArea step = new JTextArea();
    private JTextArea heads = new JTextArea();
    private JTextArea tails = new JTextArea();
    private JTextArea unhappy = new JTextArea();
    private JTextArea happy = new JTextArea();
    private JTextArea empty = new JTextArea();
    private JTextArea segregated = new JTextArea();

    public SegregationFrame() {
        setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));

        //제일먼저 하우스(JButton)들이 들어갈 northpanel을 초기화 해준다. 색상은 이후에 변경.
        northPanel = new JPanel();
        northPanel.setPreferredSize(new Dimension(650,650));
        northPanel.setLayout(new GridLayout(Segregation.getSize(), Segregation.getSize()));
        smallPanelArr = new JButton[Segregation.getSize()][Segregation.getSize()];
        for(int i = 0; i < Segregation.getSize(); i++) {
            for(int j = 0; j < Segregation.getSize(); j++) {
                 smallPanelArr[i][j]= new JButton();
                 northPanel.add(smallPanelArr[i][j]);                
            }
        }

        //초기화면의 segregation을 구성함
        s = new Segregation(Segregation.getSize(), Segregation.getEmptyRate());
        s.updateState(tolerance, diversity);
        stepCount = 0;
        makeSegregationDisp(s.getCity());

        //메인화면 밑의 data information에 대한 centerPanel구현
        centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(650,150));

        //centerPanel안에 centerWestPanel을 만들어줌
        JPanel centerWestPanel = new JPanel();
        
        centerWestPanel.setPreferredSize(new Dimension(300,100));
        centerWestPanel.setLayout(new GridLayout(4,4));
        makeInfoDisp(s, centerWestPanel, size, "Size", false);
        makeInfoDisp(s, centerWestPanel, step, "Step", false);
        makeInfoDisp(s, centerWestPanel, heads, "Heads", true);
        makeInfoDisp(s, centerWestPanel, unhappy, "UnHappy", false);
        makeInfoDisp(s, centerWestPanel, tails, "Tails", true);
        makeInfoDisp(s, centerWestPanel, happy, "Happy", false);
        makeInfoDisp(s, centerWestPanel, empty, "Empty", false);
        makeInfoDisp(s, centerWestPanel, segregated, "Segregated", false);
        //지금까지 centerWestPanel을 구현한걸 centerPanel에 추가해줌
        centerPanel.add(centerWestPanel,BorderLayout.WEST);

        //centerPanel안에 centerEastPanel을 만들어줌
        JPanel centerEastPanel = new JPanel();
        centerEastPanel.setPreferredSize(new Dimension(300,100));
        //Tolerance에 대한 정보를 담을 Panel을 만들어줌
        JPanel tolerancePanel = new JPanel();
        JSlider toleranceSlider = new JSlider(JSlider.HORIZONTAL,0, 100, (int)(tolerance*(double)100));
        JTextArea toleranceDisp = new JTextArea(String.valueOf(tolerance*100)+"%");
        toleranceSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                tolerance = ((double)toleranceSlider.getValue())/(double)100;
                toleranceDisp.setText(String.valueOf(toleranceSlider.getValue())+"%");
            }
        });
        tolerancePanel.add(new JLabel("Tolerance"));
        tolerancePanel.add(toleranceSlider);
        tolerancePanel.add(toleranceDisp);
        centerEastPanel.add(tolerancePanel, BorderLayout.NORTH);
        //diversity select 박스를 만들어줌
        JCheckBox diversityCheck = new JCheckBox("Diversity (at least 1 must be different)");
        diversityCheck.setSelected(false);
        diversityCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(diversityCheck.isSelected()) diversity = true;
                else diversity = false;
            }
        });
        centerEastPanel.add(diversityCheck, BorderLayout.CENTER);

        centerPanel.add(centerEastPanel, BorderLayout.EAST);

        //버튼을 담을 southPanel을 초기화해준다. 
        southPanel = new JPanel();
        southPanel.setPreferredSize(new Dimension(650,50));

        //reset버튼을 누를때 마다 새로운 Segregation의 객체를 생성하면서 새로운 city의 구성을 만들어 준다.
        JButton button1 = new JButton("reset");
        button1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                s = new Segregation(Segregation.getSize(), Segregation.getEmptyRate());
                s.updateState(tolerance, diversity);
                makeSegregationDisp(s.getCity());
                stepCount = 0;
                DataUpdate(s, stepCount);
            }
        });
        southPanel.add(button1);

        //run버튼을 누르면 끝까지 실행하도록 사실상 Segregation클래스의 main 메소드를 그대로 가져왔다.
        JButton button2 = new JButton("run");
        button2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                for (int n = 0; n < 2000; n++) {
                    s.move();
                    s.updateState(tolerance, diversity);
                    makeSegregationDisp(s.getCity());
                    stepCount++;
                    DataUpdate(s, stepCount);
                    int[] counts = s.countStates();
                    if (counts[State.UNHAPPY.ordinal()] == 0)
                    break;
                }
            }
        });
        southPanel.add(button2);

        //step버튼을 누를때 마다 한번씩 move()하도록 한다.
        JButton button3 = new JButton("step");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                s.move();
                s.updateState(tolerance, diversity);
                makeSegregationDisp(s.getCity());
                stepCount++;
                DataUpdate(s, stepCount);
            }
        });
        southPanel.add(button3);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    public void makeInfoDisp(Segregation s, JPanel panel, JTextArea textArea, String title, boolean background) {
        panel.add(new JLabel(title, SwingConstants.RIGHT) {
        @Override
        public void setBackground(Color bg) {
            if(background) {
                super.setOpaque(true);
                if(title == "Heads") super.setBackground(Color.RED);
                else super.setBackground(Color.BLUE);
                super.setForeground(Color.WHITE);
            }        
        }});
                
        String context = "";
        switch (title) {
            case "Size": context = String.valueOf(Segregation.getSize());
                break;
            case "Step": context = String.valueOf(0);
                break;
            case "Heads": context = String.valueOf(s.countTypes()[0]);
                break;
            case "UnHappy": context = String.valueOf("("+printPercent(s.countStates()[1])+")"+s.countStates()[1]);
                break;
            case "Tails": context = String.valueOf(s.countTypes()[1]);
                break;
            case "Happy": context = String.valueOf("("+printPercent(s.countStates()[2])+")"+s.countStates()[2]);
                break;
            case "Empty": context = String.valueOf(s.countTypes()[2]);
                break;
            case "Segregated": context = String.valueOf("("+printPercent(s.countSegregated())+")"+s.countSegregated());
                break;
        }
        textArea.setText(context);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        panel.add(textArea);
    }

    public String printPercent(int n) {
        double percent = n/(Math.pow(Segregation.getSize(), 2)-s.countTypes()[2]) *100;
        return String.valueOf(String.format("%.2f", percent));
    }

    public void DataUpdate(Segregation s, int c) {
        step.setText(String.valueOf(c));
        heads.setText(String.valueOf(s.countTypes()[0]));
        tails.setText(String.valueOf(s.countTypes()[1]));
        empty.setText(String.valueOf(s.countTypes()[2]));
        unhappy.setText(String.valueOf("("+printPercent(s.countStates()[1])+")"+s.countStates()[1]));
        happy.setText(String.valueOf("("+printPercent(s.countStates()[2])+")"+s.countStates()[2]));
        segregated.setText(String.valueOf("("+printPercent(s.countSegregated())+")"+s.countSegregated()));
    }
    
    public void makeSegregationDisp(House[][] city) {
        for(int i = 0; i < Segregation.getSize(); i++) {
            for(int j = 0; j < Segregation.getSize(); j++) {
                smallPanelArr[i][j].setBackground(city[i][j].getType().getColor());
            }
        }
    }
}