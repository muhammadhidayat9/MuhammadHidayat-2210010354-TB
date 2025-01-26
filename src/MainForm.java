
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.JTable;

public final class MainForm extends javax.swing.JFrame {

    private int selectedTransaksiId = -1;  // ID transaksi yang dipilih (-1 berarti tidak ada yang dipilih)

    public MainForm() {
        initComponents();
        loadMaskapai();
        loadPenumpang();
        loadPenumpangToComboBox();
        loadMaskapaiToComboBox();
        loadTransaksiData();

        jTableMaskapai.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Ambil baris yang dipilih
                int selectedRow = jTableMaskapai.getSelectedRow();

                // Pastikan baris yang dipilih valid (tidak kosong)
                if (selectedRow != -1) {
                    // Ambil data dari tabel (kolom 1 untuk nama dan kolom 2 untuk kode maskapai)
                    String namaMaskapai = (String) jTableMaskapai.getValueAt(selectedRow, 1); // Kolom 1 untuk Nama Maskapai
                    String kodeMaskapai = (String) jTableMaskapai.getValueAt(selectedRow, 2); // Kolom 2 untuk Kode Maskapai

                    // Set nilai yang diambil ke dalam JTextField
                    txtNamaMaskapai.setText(namaMaskapai);  // Set Nama Maskapai ke JTextField
                    txtKodeMaskapai.setText(kodeMaskapai);  // Set Kode Maskapai ke JTextField
                }
            }
        });

        // Menambahkan MouseListener untuk jTablePenumpang
        jTablePenumpang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Ambil baris yang dipilih
                int selectedRow = jTablePenumpang.getSelectedRow();

                // Pastikan baris yang dipilih valid (tidak kosong)
                if (selectedRow != -1) {
                    // Ambil data dari tabel (kolom 1 untuk nama dan kolom 2 untuk umur penumpang)
                    String namaPenumpang = (String) jTablePenumpang.getValueAt(selectedRow, 1); // Kolom 1 untuk Nama Penumpang
                    int umurPenumpang = (int) jTablePenumpang.getValueAt(selectedRow, 2); // Kolom 2 untuk Umur Penumpang

                    // Set nilai yang diambil ke dalam JTextField
                    txtNamaPenumpang.setText(namaPenumpang);  // Set Nama Penumpang ke JTextField
                    txtUmurPenumpang.setText(String.valueOf(umurPenumpang));  // Set Umur Penumpang ke JTextField
                }
            }
        });

        jTableTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectTransaksiFromTable();
            }
        });

        // Menambahkan event listener untuk tombol Export to CSV
        btnExportCSV.addActionListener(e -> {
            exportToCSV(jTableTransaksi);  // Panggil fungsi exportToCSV dan kirim jTableTransaksi sebagai parameter
        });

        btnExportPenumpangCSV.addActionListener(e -> {
            // Panggil fungsi untuk mengekspor data Penumpang ke file CSV
            exportToCSV(jTablePenumpang);  // Panggil fungsi exportToCSV dan kirim jTablePenumpang sebagai parameter
        });

        btnExportMaskapaiCSV.addActionListener(e -> {
            // Panggil fungsi untuk mengekspor data Maskapai ke file CSV
            exportToCSV(jTableMaskapai);  // Panggil fungsi exportToCSV dan kirim jTableMaskapai sebagai parameter
        });

    }

    public void exportToCSV(JTable table) {
        try {
            // Pilih lokasi untuk menyimpan file CSV
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            fileChooser.setSelectedFile(new java.io.File(".csv"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return;  // User cancels, don't proceed
            }

            java.io.File fileToSave = fileChooser.getSelectedFile();
            // Open the file to write CSV
            PrintWriter writer = new PrintWriter(new FileWriter(fileToSave));

            // Get the table model
            javax.swing.table.TableModel model = table.getModel();

            // Write column names to CSV file
            for (int col = 0; col < model.getColumnCount(); col++) {
                writer.print(model.getColumnName(col));
                if (col < model.getColumnCount() - 1) {
                    writer.print(",");  // Column separator
                }
            }
            writer.println();  // Move to the next line after column names

            // Write row data to CSV file
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    writer.print(model.getValueAt(row, col));
                    if (col < model.getColumnCount() - 1) {
                        writer.print(",");  // Column separator
                    }
                }
                writer.println();  // Move to the next line after each row
            }

            writer.flush();
            writer.close();

            JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke CSV.", "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menulis ke file CSV.", "Export Failed", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void selectTransaksiFromTable() {
        int selectedRow = jTableTransaksi.getSelectedRow();

        // Pastikan ada baris yang dipilih
        if (selectedRow != -1) {
            // Ambil data dari tabel berdasarkan kolom
            int id = (int) jTableTransaksi.getValueAt(selectedRow, 0);  // Kolom ID
            String maskapai = (String) jTableTransaksi.getValueAt(selectedRow, 1);  // Kolom Maskapai
            String penumpang = (String) jTableTransaksi.getValueAt(selectedRow, 2);  // Kolom Penumpang
            java.util.Date tanggal = (java.util.Date) jTableTransaksi.getValueAt(selectedRow, 3);  // Kolom Tanggal
            double harga = (double) jTableTransaksi.getValueAt(selectedRow, 4);  // Kolom Harga

            // Isi field input dengan data yang diambil
            comboBoxMaskapai.setSelectedItem(maskapai);
            comboBoxPenumpang.setSelectedItem(penumpang);
            dateChooser.setDate(tanggal);
            txtHargaTiket.setText(String.valueOf(harga));

            // Simpan ID transaksi untuk digunakan saat update
            selectedTransaksiId = id;
        }
    }

    public void loadMaskapai() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM maskapai";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Ambil metadata kolom untuk nama kolom
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Membuat array untuk nama kolom
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnLabel(i); // Menyimpan nama kolom
            }

            // Model tabel untuk menampilkan data
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Ambil data dari ResultSet dan tampilkan di JTable
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);  // Menyimpan data ke dalam array
                }
                model.addRow(row); // Menambahkan baris ke tabel
            }

            // Set model ke JTable
            jTableMaskapai.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPenumpang() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM penumpang";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Ambil metadata kolom untuk nama kolom
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Membuat array untuk nama kolom
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnLabel(i); // Menyimpan nama kolom
            }

            // Model tabel untuk menampilkan data
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Ambil data dari ResultSet dan tampilkan di JTable
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);  // Menyimpan data ke dalam array
                }
                model.addRow(row); // Menambahkan baris ke tabel
            }

            // Set model ke JTable
            jTablePenumpang.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadMaskapaiToComboBox() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM maskapai";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Clear existing data in ComboBox
            comboBoxMaskapai.removeAllItems();

            // Menambahkan data maskapai ke comboBox
            while (rs.next()) {
                String namaMaskapai = rs.getString("nama_maskapai");
                comboBoxMaskapai.addItem(namaMaskapai);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPenumpangToComboBox() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM penumpang";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Clear existing data in ComboBox
            comboBoxPenumpang.removeAllItems();

            // Menambahkan data penumpang ke comboBox
            while (rs.next()) {
                String namaPenumpang = rs.getString("nama_penumpang");
                comboBoxPenumpang.addItem(namaPenumpang);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMaskapai() {
        String namaMaskapai = txtNamaMaskapai.getText();
        String kodeMaskapai = txtKodeMaskapai.getText();

        // Validasi input
        if (namaMaskapai.isEmpty() || kodeMaskapai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Kode Maskapai harus diisi.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO maskapai (nama_maskapai, kode_maskapai) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, namaMaskapai);
            stmt.setString(2, kodeMaskapai);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Maskapai berhasil ditambahkan.");
            loadMaskapai();  // Refresh tabel setelah data ditambahkan
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menambahkan maskapai.");
        }
    }

    public void addPenumpang() {
        String namaPenumpang = txtNamaPenumpang.getText();
        String umurPenumpang = txtUmurPenumpang.getText();

        // Validasi input: hanya memeriksa field untuk penumpang
        if (namaPenumpang.isEmpty() || umurPenumpang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Umur Penumpang harus diisi.");
            return;
        }

        // Jika semua input valid, lanjutkan untuk menambah penumpang ke database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO penumpang (nama_penumpang, umur) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, namaPenumpang);
            stmt.setInt(2, Integer.parseInt(umurPenumpang));  // Convert umur ke integer
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Penumpang berhasil ditambahkan.");
            loadPenumpang();  // Refresh tabel setelah data ditambahkan
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menambahkan penumpang.");
        }
    }

    public void loadTransaksiData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT tiket.id, maskapai.nama_maskapai, penumpang.nama_penumpang, tiket.tanggal_pembelian, tiket.harga "
                    + "FROM tiket "
                    + "JOIN maskapai ON tiket.id_maskapai = maskapai.id "
                    + "JOIN penumpang ON tiket.id_penumpang = penumpang.id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Menyiapkan nama kolom untuk tabel
            String[] columnNames = {"ID", "Maskapai", "Penumpang", "Tanggal Pembelian", "Harga"};

            // Membuat DefaultTableModel dengan nama kolom
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Mengosongkan tabel sebelumnya sebelum menambahkan data baru
            model.setRowCount(0); // Clear previous rows

            // Menambahkan data hasil query ke dalam tabel
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama_maskapai"),
                    rs.getString("nama_penumpang"),
                    rs.getDate("tanggal_pembelian"),
                    rs.getDouble("harga")
                };
                model.addRow(row);  // Menambahkan row ke model tabel
            }

            // Menetapkan model ke jTableTransaksi
            jTableTransaksi.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTransaksi() {
        // Ambil data dari input
        String namaMaskapai = (String) comboBoxMaskapai.getSelectedItem();
        String namaPenumpang = (String) comboBoxPenumpang.getSelectedItem();
        String hargaTiket = txtHargaTiket.getText();
        java.util.Date utilDate = dateChooser.getDate();  // Ambil tanggal dari DateChooser

        // Validasi input
        if (namaMaskapai == null || namaPenumpang == null || hargaTiket.isEmpty() || utilDate == null) {
            JOptionPane.showMessageDialog(this, "Semua data harus diisi.");
            return;
        }

        // Konversi java.util.Date ke java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ambil id maskapai dan id penumpang berdasarkan nama
            String queryIdMaskapai = "SELECT id FROM maskapai WHERE nama_maskapai = ?";
            PreparedStatement stmtMaskapai = conn.prepareStatement(queryIdMaskapai);
            stmtMaskapai.setString(1, namaMaskapai);
            ResultSet rsMaskapai = stmtMaskapai.executeQuery();
            rsMaskapai.next();
            int idMaskapai = rsMaskapai.getInt("id");

            String queryIdPenumpang = "SELECT id FROM penumpang WHERE nama_penumpang = ?";
            PreparedStatement stmtPenumpang = conn.prepareStatement(queryIdPenumpang);
            stmtPenumpang.setString(1, namaPenumpang);
            ResultSet rsPenumpang = stmtPenumpang.executeQuery();
            rsPenumpang.next();
            int idPenumpang = rsPenumpang.getInt("id");

            // Insert transaksi ke tabel tiket
            String queryInsert = "INSERT INTO tiket (id_maskapai, id_penumpang, tanggal_pembelian, harga) VALUES (?, ?, ?, ?)";
            PreparedStatement stmtTransaksi = conn.prepareStatement(queryInsert);
            stmtTransaksi.setInt(1, idMaskapai);
            stmtTransaksi.setInt(2, idPenumpang);
            stmtTransaksi.setDate(3, sqlDate);  // Menggunakan java.sql.Date
            stmtTransaksi.setDouble(4, Double.parseDouble(hargaTiket));  // Pastikan harga tiket dalam format numerik
            stmtTransaksi.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan.");
            loadTransaksiData();  // Refresh data tabel setelah transaksi disimpan
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan transaksi.");
        }
    }

    public void updateMaskapai() {
        int selectedRow = jTableMaskapai.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih maskapai yang akan diperbarui.");
            return;
        }

        int idMaskapai = (int) jTableMaskapai.getValueAt(selectedRow, 0);  // Ambil ID maskapai yang dipilih
        String namaMaskapai = txtNamaMaskapai.getText();
        String kodeMaskapai = txtKodeMaskapai.getText();

        if (namaMaskapai.isEmpty() || kodeMaskapai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Kode Maskapai harus diisi.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE maskapai SET nama_maskapai = ?, kode_maskapai = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, namaMaskapai);
            stmt.setString(2, kodeMaskapai);
            stmt.setInt(3, idMaskapai);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Maskapai berhasil diperbarui.");
            loadMaskapai();  // Refresh tabel setelah data diperbarui
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui maskapai.");
        }
    }

    public void updatePenumpang() {
        int selectedRow = jTablePenumpang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penumpang yang akan diperbarui.");
            return;
        }

        int idPenumpang = (int) jTablePenumpang.getValueAt(selectedRow, 0);  // Ambil ID penumpang yang dipilih
        String namaPenumpang = txtNamaPenumpang.getText();
        String umurPenumpang = txtUmurPenumpang.getText();

        if (namaPenumpang.isEmpty() || umurPenumpang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Umur Penumpang harus diisi.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE penumpang SET nama_penumpang = ?, umur = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, namaPenumpang);
            stmt.setInt(2, Integer.parseInt(umurPenumpang));  // Convert umur ke integer
            stmt.setInt(3, idPenumpang);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Penumpang berhasil diperbarui.");
            loadPenumpang();  // Refresh tabel setelah data diperbarui
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui penumpang.");
        }
    }

    public void updateTransaksi() {
        // Validasi input
        if (selectedTransaksiId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan diperbarui.");
            return;
        }

        String namaMaskapai = (String) comboBoxMaskapai.getSelectedItem();
        String namaPenumpang = (String) comboBoxPenumpang.getSelectedItem();
        String hargaTiket = txtHargaTiket.getText();
        java.util.Date utilDate = dateChooser.getDate();

        if (namaMaskapai == null || namaPenumpang == null || hargaTiket.isEmpty() || utilDate == null) {
            JOptionPane.showMessageDialog(this, "Semua data harus diisi.");
            return;
        }

        // Konversi java.util.Date ke java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ambil ID maskapai dan penumpang
            String queryIdMaskapai = "SELECT id FROM maskapai WHERE nama_maskapai = ?";
            PreparedStatement stmtMaskapai = conn.prepareStatement(queryIdMaskapai);
            stmtMaskapai.setString(1, namaMaskapai);
            ResultSet rsMaskapai = stmtMaskapai.executeQuery();
            rsMaskapai.next();
            int idMaskapai = rsMaskapai.getInt("id");

            String queryIdPenumpang = "SELECT id FROM penumpang WHERE nama_penumpang = ?";
            PreparedStatement stmtPenumpang = conn.prepareStatement(queryIdPenumpang);
            stmtPenumpang.setString(1, namaPenumpang);
            ResultSet rsPenumpang = stmtPenumpang.executeQuery();
            rsPenumpang.next();
            int idPenumpang = rsPenumpang.getInt("id");

            // Update data di tabel tiket
            String queryUpdate = "UPDATE tiket SET id_maskapai = ?, id_penumpang = ?, tanggal_pembelian = ?, harga = ? WHERE id = ?";
            PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate);
            stmtUpdate.setInt(1, idMaskapai);
            stmtUpdate.setInt(2, idPenumpang);
            stmtUpdate.setDate(3, sqlDate);
            stmtUpdate.setDouble(4, Double.parseDouble(hargaTiket));
            stmtUpdate.setInt(5, selectedTransaksiId);
            stmtUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil diperbarui.");
            loadTransaksiData();  // Refresh data tabel
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui transaksi.");
        }
    }

    public void deleteMaskapai() {
        int selectedRow = jTableMaskapai.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih maskapai yang akan dihapus.");
            return;
        }

        int idMaskapai = (int) jTableMaskapai.getValueAt(selectedRow, 0);  // Ambil ID maskapai yang dipilih

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus maskapai ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM maskapai WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, idMaskapai);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Maskapai berhasil dihapus.");
                loadMaskapai();  // Refresh tabel setelah data dihapus
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus maskapai.");
            }
        }
    }

    public void deletePenumpang() {
        int selectedRow = jTablePenumpang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penumpang yang akan dihapus.");
            return;
        }

        int idPenumpang = (int) jTablePenumpang.getValueAt(selectedRow, 0);  // Ambil ID penumpang yang dipilih

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus penumpang ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM penumpang WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, idPenumpang);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Penumpang berhasil dihapus.");
                loadPenumpang();  // Refresh tabel setelah data dihapus
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus penumpang.");
            }
        }
    }

    public void deleteTransaksi() {
        // Pastikan ada transaksi yang dipilih
        if (selectedTransaksiId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dihapus.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Hapus transaksi berdasarkan ID
                String queryDelete = "DELETE FROM tiket WHERE id = ?";
                PreparedStatement stmtDelete = conn.prepareStatement(queryDelete);
                stmtDelete.setInt(1, selectedTransaksiId);
                stmtDelete.executeUpdate();

                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus.");
                loadTransaksiData();  // Refresh data tabel
                resetForm();  // Kosongkan form
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus transaksi.");
            }
        }
    }

    public void resetMaskapai() {
        // Mengosongkan JTextField
        txtNamaMaskapai.setText("");  // Reset input Nama Maskapai
        txtKodeMaskapai.setText("");  // Reset input Kode Maskapai

        // Mengatur ulang pilihan di JTable (membuang seleksi yang ada)
        jTableMaskapai.clearSelection();  // Hapus seleksi yang ada di tabel
    }

    public void resetPenumpang() {
        txtNamaPenumpang.setText("");  // Reset Nama Penumpang
        txtUmurPenumpang.setText("");  // Reset Umur Penumpang
        jTablePenumpang.clearSelection();  // Hapus seleksi pada tabel
    }

    public void resetForm() {
        // Mengosongkan ComboBox untuk Maskapai dan Penumpang
        comboBoxMaskapai.setSelectedIndex(-1);  // Set ke index -1 (tidak ada pilihan)
        comboBoxPenumpang.setSelectedIndex(-1);  // Set ke index -1 (tidak ada pilihan)

        // Mengosongkan DateChooser (tanggal pembelian)
        dateChooser.setDate(null);  // Mengosongkan tanggal yang dipilih

        // Mengosongkan TextField Harga Tiket
        txtHargaTiket.setText("");  // Mengosongkan field harga tiket
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMaskapai = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtKodeMaskapai = new javax.swing.JTextField();
        txtNamaMaskapai = new javax.swing.JTextField();
        btnExportMaskapaiCSV = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnDeleteMaskapai = new javax.swing.JButton();
        btnUpdateMaskapai = new javax.swing.JButton();
        btnAddMaskapai = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTablePenumpang = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNamaPenumpang = new javax.swing.JTextField();
        txtUmurPenumpang = new javax.swing.JTextField();
        btnExportPenumpangCSV = new javax.swing.JButton();
        btnAddPenumpang = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnDeletePenumpang = new javax.swing.JButton();
        btnUpdatePenumpang = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableTransaksi = new javax.swing.JTable();
        comboBoxMaskapai = new javax.swing.JComboBox<>();
        comboBoxPenumpang = new javax.swing.JComboBox<>();
        txtHargaTiket = new javax.swing.JTextField();
        btnSimpanTransaksi = new javax.swing.JButton();
        btnExportCSV = new javax.swing.JButton();
        btnResetTransaksi = new javax.swing.JButton();
        btnUpdateTransaksi = new javax.swing.JButton();
        btnDeleteTransaksi = new javax.swing.JButton();
        dateChooser = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Aplikasi Tiket Pesawat");

        jTableMaskapai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableMaskapai);

        jLabel2.setText("Kode Maskapai");

        jLabel3.setText("Nama Maskapai");

        btnExportMaskapaiCSV.setText("Report");
        btnExportMaskapaiCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportMaskapaiCSVActionPerformed(evt);
            }
        });

        jButton2.setText("Reset");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnDeleteMaskapai.setText("Hapus");
        btnDeleteMaskapai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteMaskapaiActionPerformed(evt);
            }
        });

        btnUpdateMaskapai.setText("Update");
        btnUpdateMaskapai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateMaskapaiActionPerformed(evt);
            }
        });

        btnAddMaskapai.setText("Tambah");
        btnAddMaskapai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMaskapaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(104, 104, 104)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNamaMaskapai, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                    .addComponent(txtKodeMaskapai)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnAddMaskapai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUpdateMaskapai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteMaskapai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExportMaskapaiCSV)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtKodeMaskapai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNamaMaskapai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportMaskapaiCSV)
                    .addComponent(jButton2)
                    .addComponent(btnDeleteMaskapai)
                    .addComponent(btnUpdateMaskapai)
                    .addComponent(btnAddMaskapai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Maskapai", jPanel2);

        jTablePenumpang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTablePenumpang);

        jLabel4.setText("Nama Penumpang");

        jLabel5.setText("Umur Penumpang");

        btnExportPenumpangCSV.setText("Report");
        btnExportPenumpangCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPenumpangCSVActionPerformed(evt);
            }
        });

        btnAddPenumpang.setText("Tambah");
        btnAddPenumpang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPenumpangActionPerformed(evt);
            }
        });

        jButton5.setText("Reset");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        btnDeletePenumpang.setText("Hapus");
        btnDeletePenumpang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePenumpangActionPerformed(evt);
            }
        });

        btnUpdatePenumpang.setText("Update");
        btnUpdatePenumpang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePenumpangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(176, 176, 176)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNamaPenumpang, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                    .addComponent(txtUmurPenumpang))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnAddPenumpang)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdatePenumpang)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeletePenumpang)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExportPenumpangCSV)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNamaPenumpang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtUmurPenumpang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportPenumpangCSV)
                    .addComponent(btnAddPenumpang)
                    .addComponent(jButton5)
                    .addComponent(btnDeletePenumpang)
                    .addComponent(btnUpdatePenumpang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Penumpang", jPanel3);

        jLabel6.setText("Maskapai");

        jLabel7.setText("Penumpang");

        jLabel8.setText("Tanggal");

        jLabel9.setText("Harga");

        jTableTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTableTransaksi);

        comboBoxMaskapai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        comboBoxPenumpang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnSimpanTransaksi.setText("Tambah");
        btnSimpanTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanTransaksiActionPerformed(evt);
            }
        });

        btnExportCSV.setText("Report");
        btnExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportCSVActionPerformed(evt);
            }
        });

        btnResetTransaksi.setText("Reset");
        btnResetTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetTransaksiActionPerformed(evt);
            }
        });

        btnUpdateTransaksi.setText("Update");
        btnUpdateTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateTransaksiActionPerformed(evt);
            }
        });

        btnDeleteTransaksi.setText("Delete");
        btnDeleteTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addGap(162, 162, 162)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(comboBoxPenumpang, 0, 231, Short.MAX_VALUE)
                                    .addComponent(comboBoxMaskapai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addGap(189, 189, 189)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHargaTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
                                .addComponent(btnUpdateTransaksi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteTransaksi))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSimpanTransaksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExportCSV)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetTransaksi)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(comboBoxMaskapai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(comboBoxPenumpang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtHargaTiket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateTransaksi)
                    .addComponent(btnDeleteTransaksi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpanTransaksi)
                    .addComponent(btnExportCSV)
                    .addComponent(btnResetTransaksi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Transaksi Tiket", jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(353, 353, 353)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(51, 51, 51)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteMaskapaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteMaskapaiActionPerformed
        deleteMaskapai();
    }//GEN-LAST:event_btnDeleteMaskapaiActionPerformed

    private void btnUpdateMaskapaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateMaskapaiActionPerformed
        updateMaskapai();
    }//GEN-LAST:event_btnUpdateMaskapaiActionPerformed

    private void btnAddMaskapaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMaskapaiActionPerformed
        addMaskapai();
    }//GEN-LAST:event_btnAddMaskapaiActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        resetMaskapai();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnAddPenumpangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPenumpangActionPerformed
        addPenumpang();
    }//GEN-LAST:event_btnAddPenumpangActionPerformed

    private void btnUpdatePenumpangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdatePenumpangActionPerformed
        updatePenumpang();
    }//GEN-LAST:event_btnUpdatePenumpangActionPerformed

    private void btnDeletePenumpangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePenumpangActionPerformed
        deletePenumpang();
    }//GEN-LAST:event_btnDeletePenumpangActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        resetPenumpang();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnSimpanTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanTransaksiActionPerformed
        saveTransaksi();
    }//GEN-LAST:event_btnSimpanTransaksiActionPerformed

    private void btnResetTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetTransaksiActionPerformed
        resetForm();
    }//GEN-LAST:event_btnResetTransaksiActionPerformed

    private void btnUpdateTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateTransaksiActionPerformed
        updateTransaksi();
    }//GEN-LAST:event_btnUpdateTransaksiActionPerformed

    private void btnDeleteTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTransaksiActionPerformed
        deleteTransaksi();
    }//GEN-LAST:event_btnDeleteTransaksiActionPerformed

    private void btnExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportCSVActionPerformed


    }//GEN-LAST:event_btnExportCSVActionPerformed

    private void btnExportPenumpangCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPenumpangCSVActionPerformed


    }//GEN-LAST:event_btnExportPenumpangCSVActionPerformed

    private void btnExportMaskapaiCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportMaskapaiCSVActionPerformed


    }//GEN-LAST:event_btnExportMaskapaiCSVActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMaskapai;
    private javax.swing.JButton btnAddPenumpang;
    private javax.swing.JButton btnDeleteMaskapai;
    private javax.swing.JButton btnDeletePenumpang;
    private javax.swing.JButton btnDeleteTransaksi;
    private javax.swing.JButton btnExportCSV;
    private javax.swing.JButton btnExportMaskapaiCSV;
    private javax.swing.JButton btnExportPenumpangCSV;
    private javax.swing.JButton btnResetTransaksi;
    private javax.swing.JButton btnSimpanTransaksi;
    private javax.swing.JButton btnUpdateMaskapai;
    private javax.swing.JButton btnUpdatePenumpang;
    private javax.swing.JButton btnUpdateTransaksi;
    private javax.swing.JComboBox<String> comboBoxMaskapai;
    private javax.swing.JComboBox<String> comboBoxPenumpang;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableMaskapai;
    private javax.swing.JTable jTablePenumpang;
    private javax.swing.JTable jTableTransaksi;
    private javax.swing.JTextField txtHargaTiket;
    private javax.swing.JTextField txtKodeMaskapai;
    private javax.swing.JTextField txtNamaMaskapai;
    private javax.swing.JTextField txtNamaPenumpang;
    private javax.swing.JTextField txtUmurPenumpang;
    // End of variables declaration//GEN-END:variables
}
