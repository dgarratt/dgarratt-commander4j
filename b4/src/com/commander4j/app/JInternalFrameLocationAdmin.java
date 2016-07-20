package com.commander4j.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.commander4j.db.JDBLanguage;
import com.commander4j.db.JDBLocation;
import com.commander4j.db.JDBMaterialUom;
import com.commander4j.db.JDBQuery2;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JMenu4j;
import com.commander4j.gui.JMenuItem4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.sys.Common;
import com.commander4j.sys.JLaunchMenu;
import com.commander4j.sys.JLaunchReport;
import com.commander4j.tablemodel.JDBLocationTableModel;
import com.commander4j.util.JExcel;
import com.commander4j.util.JHelp;
import com.commander4j.util.JUtility;

public class JInternalFrameLocationAdmin extends JInternalFrame
{
	private JButton4j jButtonClear;
	private JLabel4j_std jStatusText;
	private static final long serialVersionUID = 1;
	private JDesktopPane jDesktopPane1;
	private JTextField4j jTextFieldPlant;
	private JLabel4j_std jLabel2;
	private JLabel4j_std jLabel4;
	private JButton4j jButtonClose;
	private JButton4j jButtonHelp;
	private JButton4j jButtonPrint;
	private JButton4j jButtonDelete;
	private JButton4j jButtonEdit;
	private JButton4j jButtonAdd;
	private JLabel4j_std jLabel8;
	private JToggleButton jToggleButtonSequence;
	private JButton4j jButtonSearch;
	private JComboBox4j<String> jComboBoxSortBy;
	private JLabel4j_std jLabel10;
	private JTextField4j jTextFieldStorageLocation;
	private JTextField4j jTextFieldStorageType;
	private JLabel4j_std jLabel9;
	private JTextField4j jTextFieldStorageSection;
	private JLabel4j_std jLabel7;
	private JTextField4j jTextFieldGLN;
	private JLabel4j_std jLabel6;
	private JLabel4j_std jLabel5;
	private JTextField4j jTextFieldLocationID;
	private JTextField4j jTextFieldStorageBin;
	private JTextField4j jTextFieldWarehouse;
	private JTable jTable1;
	private JScrollPane jScrollPane1;
	private JTextField4j jTextFieldDescription;
	private JLabel4j_std jLabel3;
	private JLabel4j_std jLabel1;
	private String llocation;
	String schemaName = Common.hostList.getHost(Common.selectedHostID).getDatabaseParameters().getjdbcDatabaseSchema();
	private static boolean dlg_sort_descending = false;
	private JDBLanguage lang;
	private PreparedStatement listStatement;

	private void clearFilter() {
		jTextFieldLocationID.setText("");
		jTextFieldPlant.setText("");
		jTextFieldWarehouse.setText("");
		jTextFieldGLN.setText("");
		jTextFieldDescription.setText("");
		jTextFieldStorageLocation.setText("");
		jTextFieldStorageType.setText("");
		jTextFieldStorageSection.setText("");
		jTextFieldStorageBin.setText("");
		search();
	}

	private void filterBy(String fieldname) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			if (fieldname.equals("LOCATION_ID") == true)
			{
				jTextFieldLocationID.setText(jTable1.getValueAt(row, 0).toString());
			}

			if (fieldname.equals("PLANT") == true)
			{
				jTextFieldPlant.setText(jTable1.getValueAt(row, 1).toString());
			}

			if (fieldname.equals("WAREHOUSE") == true)
			{
				jTextFieldWarehouse.setText(jTable1.getValueAt(row, 2).toString());
			}

			if (fieldname.equals("GLN") == true)
			{
				jTextFieldGLN.setText(jTable1.getValueAt(row, 3).toString());
			}

			if (fieldname.equals("DESCRIPTION") == true)
			{
				jTextFieldDescription.setText(jTable1.getValueAt(row, 4).toString());
			}

			if (fieldname.equals("STORAGE_LOCATON") == true)
			{
				jTextFieldStorageLocation.setText(jTable1.getValueAt(row, 5).toString());
			}

			if (fieldname.equals("STORAGE_TYPE") == true)
			{
				jTextFieldStorageType.setText(jTable1.getValueAt(row, 6).toString());
			}

			if (fieldname.equals("STORAGE_SECTION") == true)
			{
				jTextFieldStorageSection.setText(jTable1.getValueAt(row, 7).toString());
			}

			if (fieldname.equals("STORAGE_BIN") == true)
			{
				jTextFieldStorageBin.setText(jTable1.getValueAt(row, 8).toString());
			}

			search();

		}
	}

	private void sortBy(String fieldname) {
		jComboBoxSortBy.setSelectedItem(fieldname);
		search();
	}

	private void print() {

		PreparedStatement temp = buildSQLr();
		JLaunchReport.runReport("RPT_LOCATIONS", null, "", temp, "");
	}

	private void search() {
		buildSQL();
		populateList();
	}

	private void addRecord() {
		JDBLocation l = new JDBLocation(Common.selectedHostID, Common.sessionID);
		llocation = (String) JOptionPane.showInputDialog(Common.mainForm, "Enter new location", null, JOptionPane.QUESTION_MESSAGE,Common.icon_confirm, null, null);
		if (llocation != null)
		{
			if (llocation.equals("") == false)
			{
				llocation = llocation.toUpperCase();
				l.setLocationID(llocation);
				if (l.isValidLocation() == false)
				{
					JLaunchMenu.runForm("FRM_ADMIN_LOCATION_EDIT", llocation);
				}
				else
				{
					JOptionPane.showMessageDialog(Common.mainForm, "Location [" + llocation + "] already exists", lang.get("err_Error"), JOptionPane.ERROR_MESSAGE,Common.icon_confirm);
				}
				buildSQL();
				populateList();
			}
		}

	}

	private void populateList() {
		JDBLocation location = new JDBLocation(Common.selectedHostID, Common.sessionID);
		JDBLocationTableModel locationtable = new JDBLocationTableModel(location.getLocationDataResultSet(listStatement));
		TableRowSorter<JDBLocationTableModel> sorter = new TableRowSorter<JDBLocationTableModel>(locationtable);

		jTable1.setRowSorter(sorter);
		jTable1.setModel(locationtable);

		jScrollPane1.setViewportView(jTable1);
		JUtility.scrolltoHomePosition(jScrollPane1);
		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTable1.setFont(Common.font_list);

		TableColumn col = jTable1.getColumnModel().getColumn(0);
		col.setPreferredWidth(100);
		col = jTable1.getColumnModel().getColumn(1);
		col.setPreferredWidth(50);
		col = jTable1.getColumnModel().getColumn(2);
		col.setPreferredWidth(80);
		col = jTable1.getColumnModel().getColumn(3);
		col.setPreferredWidth(90);
		col = jTable1.getColumnModel().getColumn(4);
		col.setPreferredWidth(120);
		col = jTable1.getColumnModel().getColumn(5);
		col.setPreferredWidth(150);
		col = jTable1.getColumnModel().getColumn(6);
		col.setPreferredWidth(100);
		col = jTable1.getColumnModel().getColumn(7);
		col.setPreferredWidth(100);
		col = jTable1.getColumnModel().getColumn(8);
		col.setPreferredWidth(100);
		col = jTable1.getColumnModel().getColumn(9);
		col.setPreferredWidth(100);
		jScrollPane1.repaint();

		JUtility.setResultRecordCountColour(jStatusText, false, 0, locationtable.getRowCount());
	}

	private void deleteRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			llocation = jTable1.getValueAt(row, 0).toString();
			int n = JOptionPane.showConfirmDialog(Common.mainForm, lang.get("dlg_Location_Delete")+" " + llocation , lang.get("dlg_Confirm"), JOptionPane.YES_NO_OPTION, 0, Common.icon_confirm);
			if (n == 0)
			{
				JDBLocation l = new JDBLocation(Common.selectedHostID, Common.sessionID);
				l.setLocationID(llocation);
				boolean result = l.delete();
				if (result == false)
				{
					JUtility.errorBeep();
					JOptionPane.showMessageDialog(Common.mainForm, l.getErrorMessage(), "Delete error (" + llocation + ")", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					buildSQL();
					populateList();
				}
			}
		}
	}

	private void editRecord() {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			llocation = jTable1.getValueAt(row, 0).toString();
			JLaunchMenu.runForm("FRM_ADMIN_LOCATION_EDIT", llocation);
		}
	}

	private void setSequence(boolean descending) {
		jToggleButtonSequence.setSelected(descending);
		if (jToggleButtonSequence.isSelected())
		{
			jToggleButtonSequence.setToolTipText("Descending");
			jToggleButtonSequence.setIcon(Common.icon_descending);
		}
		else
		{
			jToggleButtonSequence.setToolTipText("Ascending");
			jToggleButtonSequence.setIcon(Common.icon_ascending);
		}
	}

	private PreparedStatement buildSQLr() {
				
		PreparedStatement result;
		JDBQuery2 q2 = new JDBQuery2(Common.selectedHostID,Common.sessionID);
		q2.applyWhat("*");
		q2.applyFrom("{schema}APP_LOCATION");
		q2.applyWhere("location_id=", jTextFieldLocationID.getText());;
		q2.applyWhere("plant=", jTextFieldPlant.getText());
		q2.applyWhere("warehouse=", jTextFieldWarehouse.getText());
		if (jTextFieldDescription.getText().equals("") == false)
		{
			q2.applyWhere("upper(description) LIKE ", "%" + jTextFieldDescription.getText().toUpperCase() + "%");
		}
		q2.applyWhere("storage_location=", jTextFieldStorageLocation.getText());
		q2.applyWhere("storage_type=", jTextFieldStorageType.getText());
		q2.applyWhere("storage_section=", jTextFieldStorageSection.getText());
		q2.applyWhere("storage_bin=", jTextFieldStorageBin.getText());
		q2.applyWhere("gln=", jTextFieldGLN.getText());
		
		q2.applySort(jComboBoxSortBy.getSelectedItem().toString(), jToggleButtonSequence.isSelected());
		q2.applyRestriction(false, 0);
		q2.applySQL();
		result = q2.getPreparedStatement();
		
		return result;
	}	
	
	private void buildSQL() {
		
		JDBQuery2.closeStatement(listStatement);
		
		listStatement = buildSQLr();
	}

	public JInternalFrameLocationAdmin()
	{
		super();

		lang = new JDBLanguage(Common.selectedHostID, Common.sessionID);

		initGUI();
		
		
		////////
		JDBQuery2 q2 = new JDBQuery2(Common.selectedHostID,Common.sessionID);
		q2.applyWhat("*");
		q2.applyFrom("{schema}APP_LOCATION WHERE 1=2");
		q2.applyRestriction(false, 0);
		q2.applySort(jComboBoxSortBy.getSelectedItem().toString(), jToggleButtonSequence.isSelected());
		q2.applySQL();
		listStatement = q2.getPreparedStatement();
		////////
		
		populateList();

		final JHelp help = new JHelp();
		help.enableHelpOnButton(jButtonHelp, JUtility.getHelpSetIDforModule("FRM_ADMIN_LOCATIONS"));

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

		setSequence(dlg_sort_descending);
	}

	private void initGUI() {
		try
		{
			this.setPreferredSize(new java.awt.Dimension(770, 478));
			this.setBounds(0, 0, 966, 560);
			setVisible(true);
			this.setClosable(true);
			this.setIconifiable(true);
			this.setTitle("Location Administration");
			{
				jDesktopPane1 = new JDesktopPane();
				jDesktopPane1.setBackground(Common.color_app_window);
				getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
				jDesktopPane1.setLayout(null);
				{
					jLabel1 = new JLabel4j_std();
					jDesktopPane1.add(jLabel1);
					jLabel1.setText(lang.get("lbl_Storage_Plant"));
					jLabel1.setBounds(256, 11, 129, 21);
					jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					JButton4j btnExcel = new JButton4j(Common.icon_XLS);
					btnExcel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							export();
						}
					});
					btnExcel.setText(lang.get("btn_Excel"));
					btnExcel.setMnemonic(lang.getMnemonicChar());
					btnExcel.setBounds(628, 110, 103, 32);
					jDesktopPane1.add(btnExcel);
				}
				{
					jTextFieldPlant = new JTextField4j(JDBLocation.field_plant);
					jDesktopPane1.add(jTextFieldPlant);
					jTextFieldPlant.setBounds(392, 11, 80, 21);
				}
				{
					jLabel3 = new JLabel4j_std();
					jDesktopPane1.add(jLabel3);
					jLabel3.setText(lang.get("lbl_Description"));
					jLabel3.setBounds(12, 77, 91, 21);
					jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldDescription = new JTextField4j(JDBLocation.field_description);
					jDesktopPane1.add(jTextFieldDescription);
					jTextFieldDescription.setBounds(112, 77, 360, 21);
				}
				{
					jScrollPane1 = new JScrollPane();
					jScrollPane1.getViewport().setBackground(Common.color_tablebackground);
					jDesktopPane1.add(jScrollPane1);
					jScrollPane1.setBounds(0, 150, 946, 335);
					{
						TableModel jTable1Model = new DefaultTableModel(new String[][] { { "One", "Two" }, { "Three", "Four" } }, new String[] { "Column 1", "Column 2" });
						jTable1 = new JTable();
						jTable1.setDefaultRenderer(Object.class, Common.renderer_table);
						jScrollPane1.setViewportView(jTable1);
						jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						jTable1.getTableHeader().setFont(Common.font_table_header);
						jTable1.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent evt) {
								if (evt.getClickCount() == 2)
								{
									if (Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LOCATION_EDIT"))
									{
										editRecord();
									}
								}
							}
						});
						jTable1.setModel(jTable1Model);

						{
							final JPopupMenu popupMenu = new JPopupMenu();
							addPopup(jTable1, popupMenu);

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_find);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										search();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Search"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_add);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LOCATION_ADD"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										addRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Add"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_edit);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LOCATION_EDIT"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										editRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Edit"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_delete);
								newItemMenuItem.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LOCATION_DELETE"));
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										deleteRecord();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Delete"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenuItem4j newItemMenuItem = new JMenuItem4j(Common.icon_print);
								newItemMenuItem.addActionListener(new ActionListener() {
									public void actionPerformed(final ActionEvent e) {
										print();
									}
								});
								newItemMenuItem.setText(lang.get("btn_Print"));
								popupMenu.add(newItemMenuItem);
							}

							{
								final JMenu4j sortByMenu = new JMenu4j();
								sortByMenu.setText(lang.get("lbl_Sort_By"));
								popupMenu.add(sortByMenu);

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("LOCATION_ID");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Location"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("PLANT");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Plant"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("WAREHOUSE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Warehouse"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("DESCRIPTION");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Description"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("GLN");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_GLN"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("STORAGE_LOCATION");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Location"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("STORAGE_TYPE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Type"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("STORAGE_SECTION");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Section"));
									sortByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											sortBy("STORAGE_BIN");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Bin"));
									sortByMenu.add(newItemMenuItem);
								}
							}

							{
								final JMenu4j filterByMenu = new JMenu4j();
								filterByMenu.setText("Filter by");
								popupMenu.add(filterByMenu);

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("LOCATION_ID");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Location"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("PLANT");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Plant"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("WAREHOUSE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Warehouse"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("GLN");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_GLN"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("DESCRIPTION");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Description"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("STORAGE_LOCATION");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Location"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("STORAGE_TYPE");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Type"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("STORAGE_SECTION");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Section"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											filterBy("STORAGE_BIN");
										}
									});
									newItemMenuItem.setText(lang.get("lbl_Storage_Bin"));
									filterByMenu.add(newItemMenuItem);
								}

								{
									filterByMenu.addSeparator();
								}

								{
									final JMenuItem4j newItemMenuItem = new JMenuItem4j();
									newItemMenuItem.addActionListener(new ActionListener() {
										public void actionPerformed(final ActionEvent e) {
											clearFilter();
										}
									});
									newItemMenuItem.setText(lang.get("btn_Clear_Filter"));
									filterByMenu.add(newItemMenuItem);
								}
							}
						}
					}
				}
				{
					jLabel2 = new JLabel4j_std();
					jDesktopPane1.add(jLabel2);
					jLabel2.setText(lang.get("lbl_Storage_Warehouse"));
					jLabel2.setBounds(472, 11, 103, 21);
					jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldWarehouse = new JTextField4j(JDBLocation.field_warehouse);
					jDesktopPane1.add(jTextFieldWarehouse);
					jTextFieldWarehouse.setBounds(583, 11, 80, 21);
				}
				{
					jLabel4 = new JLabel4j_std();
					jDesktopPane1.add(jLabel4);
					jLabel4.setText(lang.get("lbl_Storage_Bin"));
					jLabel4.setBounds(681, 44, 113, 21);
					jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jTextFieldStorageBin = new JTextField4j(JDBLocation.field_storage_bin);
					jDesktopPane1.add(jTextFieldStorageBin);
					jTextFieldStorageBin.setBounds(801, 44, 80, 21);
				}
				{
					jTextFieldLocationID = new JTextField4j(JDBLocation.field_location_id);
					jDesktopPane1.add(jTextFieldLocationID);
					jTextFieldLocationID.setBounds(112, 11, 126, 21);
				}
				{
					jLabel5 = new JLabel4j_std();
					jDesktopPane1.add(jLabel5);
					jLabel5.setText(lang.get("lbl_Storage_Location"));
					jLabel5.setBounds(12, 11, 93, 21);
					jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
				}
				{
					jLabel6 = new JLabel4j_std();
					jDesktopPane1.add(jLabel6);
					jLabel6.setText(lang.get("lbl_Storage_GLN"));
					jLabel6.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel6.setBounds(681, 11, 114, 21);
				}
				{
					jTextFieldGLN = new JTextField4j(JDBMaterialUom.field_ean);
					jDesktopPane1.add(jTextFieldGLN);
					jTextFieldGLN.setBounds(802, 11, 126, 21);
				}
				{
					jLabel7 = new JLabel4j_std();
					jDesktopPane1.add(jLabel7);
					jLabel7.setText(lang.get("lbl_Storage_Section"));
					jLabel7.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel7.setBounds(472, 44, 103, 21);
				}
				{
					jTextFieldStorageSection = new JTextField4j(JDBLocation.field_storage_section);
					jDesktopPane1.add(jTextFieldStorageSection);
					jTextFieldStorageSection.setBounds(583, 44, 80, 21);
				}
				{
					jLabel8 = new JLabel4j_std();
					jDesktopPane1.add(jLabel8);
					jLabel8.setText(lang.get("lbl_Storage_Location"));
					jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel8.setBounds(0, 44, 105, 21);
				}
				{
					jTextFieldStorageLocation = new JTextField4j(JDBLocation.field_storage_location);
					jDesktopPane1.add(jTextFieldStorageLocation);
					jTextFieldStorageLocation.setBounds(112, 44, 126, 21);
				}
				{
					jLabel9 = new JLabel4j_std();
					jDesktopPane1.add(jLabel9);
					jLabel9.setText(lang.get("lbl_Storage_Type"));
					jLabel9.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel9.setBounds(235, 44, 149, 21);
				}
				{
					jTextFieldStorageType = new JTextField4j(JDBLocation.field_storage_type);
					jDesktopPane1.add(jTextFieldStorageType);
					jTextFieldStorageType.setBounds(391, 44, 80, 21);
				}
				{
					jLabel10 = new JLabel4j_std();
					jDesktopPane1.add(jLabel10);
					jLabel10.setText(lang.get("lbl_Sort_By"));
					jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
					jLabel10.setBounds(472, 77, 103, 21);
				}
				{
					ComboBoxModel<String> jComboBoxSortByModel = new DefaultComboBoxModel<String>(new String[] { "LOCATION_ID", "PLANT", "WAREHOUSE", "DESCRIPTION", "GLN", "STORAGE_LOCATION", "STORAGE_TYPE", "STORAGE_SECTION", "STORAGE_BIN" });
					jComboBoxSortBy = new JComboBox4j<String>();
					jDesktopPane1.add(jComboBoxSortBy);
					jComboBoxSortBy.setModel(jComboBoxSortByModel);
					jComboBoxSortBy.setBounds(582, 75, 231, 23);
					jComboBoxSortBy.setSelectedItem("LOCATION_ID,PLANT,WAREHOUSE");
					jComboBoxSortBy.setRequestFocusEnabled(false);
				}
				{
					jButtonSearch = new JButton4j(Common.icon_find);
					jDesktopPane1.add(jButtonSearch);
					jButtonSearch.setText(lang.get("btn_Search"));
					jButtonSearch.setMnemonic(lang.getMnemonicChar());
					jButtonSearch.setBounds(4, 110, 103, 32);
					jButtonSearch.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							search();

						}
					});
				}
				{
					jButtonAdd = new JButton4j(Common.icon_add);
					jDesktopPane1.add(jButtonAdd);
					jButtonAdd.setText(lang.get("btn_Add"));
					jButtonAdd.setMnemonic(lang.getMnemonicChar());
					jButtonAdd.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LOCATION_ADD"));
					jButtonAdd.setBounds(212, 110, 103, 32);
					jButtonAdd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							addRecord();
						}
					});
				}
				{
					jButtonEdit = new JButton4j(Common.icon_edit);
					jDesktopPane1.add(jButtonEdit);
					jButtonEdit.setText(lang.get("btn_Edit"));
					jButtonEdit.setMnemonic(lang.getMnemonicChar());
					jButtonEdit.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LOCATION_EDIT"));
					jButtonEdit.setBounds(316, 110, 103, 32);
					jButtonEdit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							editRecord();
						}
					});
				}
				{
					jButtonDelete = new JButton4j(Common.icon_delete);
					jDesktopPane1.add(jButtonDelete);
					jButtonDelete.setText(lang.get("btn_Delete"));
					jButtonDelete.setMnemonic(lang.getMnemonicChar());
					jButtonDelete.setEnabled(Common.userList.getUser(Common.sessionID).isModuleAllowed("FRM_ADMIN_LOCATION_DELETE"));
					jButtonDelete.setBounds(420, 110, 103, 32);
					jButtonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							deleteRecord();
						}
					});
				}
				{
					jButtonPrint = new JButton4j(Common.icon_report);
					jDesktopPane1.add(jButtonPrint);
					jButtonPrint.setText(lang.get("btn_Print"));
					jButtonPrint.setMnemonic(lang.getMnemonicChar());
					jButtonPrint.setBounds(524, 110, 103, 32);
					jButtonPrint.setEnabled(true);
					jButtonPrint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							print();
						}
					});
				}
				{
					jButtonHelp = new JButton4j(Common.icon_help);
					jDesktopPane1.add(jButtonHelp);
					jButtonHelp.setText(lang.get("btn_Help"));
					jButtonHelp.setMnemonic(lang.getMnemonicChar());
					jButtonHelp.setBounds(732, 110, 103, 32);
				}
				{

					jButtonClose = new JButton4j(Common.icon_close);
					jDesktopPane1.add(jButtonClose);
					jButtonClose.setText(lang.get("btn_Close"));
					jButtonClose.setMnemonic(lang.getMnemonicChar());
					jButtonClose.setBounds(836, 110, 103, 32);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JDBQuery2.closeStatement(listStatement);
							dispose();
						}
					});
				}
				{
					jToggleButtonSequence = new JToggleButton();
					jDesktopPane1.add(jToggleButtonSequence);
					jToggleButtonSequence.setBounds(820, 77, 21, 21);
					jToggleButtonSequence.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							setSequence(jToggleButtonSequence.isSelected());
						}
					});
				}

				{
					jStatusText = new JLabel4j_std();
					jStatusText.setForeground(new Color(255, 0, 0));
					jStatusText.setBackground(Color.GRAY);
					jStatusText.setBounds(0, 487, 941, 21);
					jDesktopPane1.add(jStatusText);
				}

				{
					jButtonClear = new JButton4j(Common.icon_clear);
					jButtonClear.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							clearFilter();
						}
					});
					jButtonClear.setText(lang.get("btn_Clear_Filter"));
					jButtonClear.setBounds(108, 110, 103, 32);
					jDesktopPane1.add(jButtonClear);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void export() {
		JDBLocation location = new JDBLocation(Common.selectedHostID, Common.sessionID);
		JExcel export = new JExcel();
		PreparedStatement temp = buildSQLr();
		export.saveAs("locations.xls", location.getLocationDataResultSet(temp), Common.mainForm);
	}

	/**
	 * WindowBuilder generated method.<br>
	 * Please don't remove this method or its invocations.<br>
	 * It used by WindowBuilder to associate the {@link javax.swing.JPopupMenu}
	 * with parent.
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
