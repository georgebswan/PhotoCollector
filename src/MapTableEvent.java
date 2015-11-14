
import java.awt.event.*;

public class MapTableEvent implements ActionListener {
	MapTablePane table;

	public MapTableEvent(MapTablePane in) {
		table = in;
	} // end of TableEvent() constructor
	
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.compareTo("New Row") == 0) {
			insertNewRow();
		}
	} // end of actionPerformed()
	
	public void insertNewRow() {
		table.dataModel.addRow(
		new Object[] {"", "", "", new Integer(0), new Boolean(false)}
		);
		table.dataModel.fireTableRowsInserted(table.dataModel.getRowCount(), table.dataModel.getRowCount());
	} // end of insertNewRow()
	
	public void removeAllRows() {
		for(int i = 0 ; i < table.dataModel.getRowCount() ; i++) {
			table.dataModel.removeRow(i);
		}
	}

} // end of class