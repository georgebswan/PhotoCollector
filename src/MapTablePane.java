import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class MapTablePane extends JPanel {
	static final long serialVersionUID = 5;
	JTable table;
	DefaultTableModel dataModel;
	JLabel label;
	int selectedRow = -1;
	
	public void setSelectedRow(int row) { selectedRow = row; }
	
	public MapTablePane () {
		super(new BorderLayout());
		
		String[] columnNames = {
				"Destination Folder",
				"",
		"To Folder",
        "Photo Count",
        "From Folder(s)",
        "Photo Count"};
		
		Object[][] data = {
				//{"None", "", "None", new Integer(0), "None", new Integer(0)}
		};
		
		//populate the table with the data
		dataModel = new DefaultTableModel();
		for (int col = 0; col < columnNames.length; col++) {
			dataModel.addColumn(columnNames[col]);
		}
		for (int row = 0; row < data.length; row++) {
			dataModel.addRow(data[row]);
		}
		
		table = new JTable(dataModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(20);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(20);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);
        table.getColumnModel().getColumn(5).setPreferredWidth(20);
		table.setPreferredScrollableViewportSize(new Dimension(720, 120));
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(Object.class, new CustomRenderer());
		
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		
		//Add the scroll pane to this panel.
		add(scrollPane);
		setOpaque(true); //content panes must be opaque
	} 
	
	public void removeAllRows() {
		for(int i = dataModel.getRowCount() - 1 ; i >= 0 ; i--) {
			dataModel.removeRow(i);
		}
	}
	
	public void addRow(String destDir, String toDir, int toCount, String fromDir, int fromCount) {
		String toCountString;
		String fromCountString;
		
		//change any zero counts to ""
		if(toCount == 0) toCountString = ""; else toCountString = Integer.toString(toCount);
		if(fromCount == 0) fromCountString = ""; else fromCountString = Integer.toString(fromCount);
		dataModel.addRow(new Object[] {destDir, "", toDir, toCountString, fromDir, fromCountString});
		dataModel.isCellEditable(dataModel.getRowCount(), 1);
		// tried to make the destination cell editable so that I can fix typos. That works, but I need to be 
		//able to read the edited cell and write that back into the database. When I click on copy, the edited text is not being used
		//dataModel.isCellEditable(dataModel.getRowCount(), 2);

		dataModel.fireTableRowsInserted(dataModel.getRowCount(), dataModel.getRowCount());
	}
	
	public void addFirstCell(String destDirName) {
		dataModel.setValueAt(destDirName, 0, 0);
	}
	
	public boolean isCellEditable(int row, int col)
    	{ return true; }
	
	class CustomRenderer implements TableCellRenderer {
		public CustomRenderer()
		{
			label = new JLabel();
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
		  //make the total row bold
		  if(row == dataModel.getRowCount() -1)
			  label.setFont(table.getFont().deriveFont(Font.BOLD));
		  else
			  label.setFont(table.getFont());
		  
		  //make the folder count cols center aligned
		  if(column == 1 || column == 3 || column == 5)
			  label.setHorizontalAlignment(JLabel.CENTER);
		  
		  //make the folder cols left aligned
		  if(column == 0 || column == 2 || column == 4)
			  label.setHorizontalAlignment(JLabel.LEFT);
		  
		  if(row <= selectedRow)
			  label.setFont(table.getFont().deriveFont(Font.BOLD));
		  
		  label.setText((String)value);
		  return label;
		}
	}
} // end of class

