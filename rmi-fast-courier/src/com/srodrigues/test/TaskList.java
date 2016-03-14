/* *
 * Based on: https://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDemoProject/src/components/ListDemo.java
 * */

package com.srodrigues.test;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.srodrigues.test.rmi.Courier;

public class TaskList extends JPanel implements ListSelectionListener {
	private static final String TITLE = "My Courier - Tasks";
	private static final String NEW   = "New";
	private static final String CLOSE = "Close";

	private static final long serialVersionUID = 1L;


	private final JList<String> list;
    private final DefaultListModel<String> listModel;

    
    private final JButton closeButton;
    private final JTextField task;

    public TaskList(Courier courier) {
        super(new BorderLayout());
        
        listModel = new DefaultListModel<String>();

        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        final JButton newButton = new JButton(NEW);
        final NewListener newListener = new NewListener(newButton, courier);
        newButton.setActionCommand(NEW);
        newButton.addActionListener(newListener);
        newButton.setEnabled(false);

        closeButton = new JButton(CLOSE);
        closeButton.setActionCommand(CLOSE);
        closeButton.addActionListener(new CloseListener(courier));
        closeButton.setEnabled(false);

        task = new JTextField(10);
        task.addActionListener(newListener);
        task.getDocument().addDocumentListener(newListener);

        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(closeButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(task);
        buttonPane.add(newButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    class CloseListener implements ActionListener {
        private final Courier courier;

        public CloseListener(final Courier courier) {
        	this.courier = courier;
		}

		public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            final String name = listModel.get(index);
			try {
				if (courier.delete(name) ){
					listModel.remove(index);
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            int size = listModel.getSize();
            if (size == 0) { 
                closeButton.setEnabled(false);
            } else {
                if (index == size ) {
                    index--;
                }
                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        }
    }

    class NewListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private final JButton button;
        private final Courier courier;

        public NewListener(final JButton button, final Courier courier) {
            this.button = button;
            this.courier = courier;
            
        }

        @Override
        public void actionPerformed(ActionEvent e) {
           final  String name = task.getText();
            // unique name...
            if (name.equals("") || listModel.contains(name)) {
                Toolkit.getDefaultToolkit().beep();
                task.requestFocusInWindow();
                task.selectAll();
                return;
            }

            int index = list.getSelectedIndex();
            if (index == -1) {
                index = 0;
            } else {          
                index++;
            }

            try {
				if (courier.add(name)){
					listModel.insertElementAt(name, index);
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            task.requestFocusInWindow();
            task.setText("");

            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
           closeButton.setEnabled(list.getSelectedIndex() != -1);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    final JFrame frame = new JFrame(TITLE);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    final JComponent newContentPane = new TaskList( new Courier() );
                    newContentPane.setOpaque(true); 
                    frame.setContentPane(newContentPane);
                    frame.pack();
                    frame.setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
}