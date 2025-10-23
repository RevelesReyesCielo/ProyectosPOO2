/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package reproductor;

/**
 *
 * @author zarat
 */
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class reproductor_mp3 extends JFrame {

    private static final Logger logger = Logger.getLogger(reproductor_mp3.class.getName());

    private Clip clip;
    private boolean paused = false;
    private int currentSongIndex = 0;
    private long clipTimePosition = 0;
    private Thread progressThread;

    private final String[] canciones = {
            "musica/cancion1.wav",
            "musica/cancion2.wav",
            "musica/cancion3.wav",
            "musica/cancion4.wav",
            "musica/cancion5.wav"
    };

    private final String[] nombres = {
            "WOS - ALMA DINAMITA",
            "BANDA MS - EL COLOR DE TUS OJOS",
            "RAUW ALEJANDRO - ALGO MAGICO",
            "TRUENO - SOLO POR VOS",
            "VICENTE FERNANDEZ - ESTOS CELOS"
    };

    private final String[] portadas = {
            "musica/portadas/portada1.jpg",
            "musica/portadas/portada2.jpg",
            "musica/portadas/portada3.jpg",
            "musica/portadas/portada4.jpg",
            "musica/portadas/portada5.jpg"
    };

    public reproductor_mp3() {
        initComponents();
        mostrarCancionActual();
        configurarEventos();
    }

    private void playSong() {
    try {
        // Si la canción está pausada
        if (paused && clip != null) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
            paused = false;
            iniciarBarraProgreso(); // lanzar el hilo de progreso
            return;
        }

        // Si ya hay un clip abierto, cerrarlo
        if (clip != null && clip.isOpen()) {
            clip.stop();
            clip.close();
        }

        // Abrir la canción actual
        File audioFile = new File(canciones[currentSongIndex]);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        clipTimePosition = 0;
        paused = false;
        clip.start();

        // Iniciar hilo de progreso
        iniciarBarraProgreso();

    } catch (Exception ex) {
        logger.log(Level.SEVERE, null, ex);
    }
}

    private void pauseSong() {
        if (clip != null && clip.isRunning()) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            paused = true;
        }
    }

   private void previousSong() {
    // Detenemos cualquier clip abierto
    if (clip != null && clip.isOpen()) {
        clip.stop();
        clip.close();
    }
    if (progressThread != null && progressThread.isAlive()) {
        progressThread.interrupt();
    }

    currentSongIndex = (currentSongIndex - 1 + canciones.length) % canciones.length;
    mostrarCancionActual(); // Resetea contador y slider
}

private void nextSong() {
    if (clip != null && clip.isOpen()) {
        clip.stop();
        clip.close();
    }
    if (progressThread != null && progressThread.isAlive()) {
        progressThread.interrupt();
    }

    currentSongIndex = (currentSongIndex + 1) % canciones.length;
    mostrarCancionActual(); // Resetea contador y slider
}

private void mostrarCancionActual() {
    jLabel1.setText(nombres[currentSongIndex]);
    ImageIcon icon = new ImageIcon(portadas[currentSongIndex]);
    ImageIcon scaled = new ImageIcon(icon.getImage().getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH));
    jLabel2.setIcon(scaled);

    // Reseteamos la barra y el tiempo
    jSlider1.setValue(0);
    jLabel3.setText("00:00 / " + formatoTiempo(getDurationOfCurrentSong()));
    
    // Reseteamos posiciones internas
    clipTimePosition = 0;
    paused = false;
}

private long getDurationOfCurrentSong() {
    try {
        File audioFile = new File(canciones[currentSongIndex]);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip tempClip = AudioSystem.getClip();
        tempClip.open(audioStream);
        long duration = tempClip.getMicrosecondLength();
        tempClip.close();
        return duration / 1_000_000; // convertir a segundos
    } catch (Exception e) {
        return 0;
    }
}

    private String formatoTiempo(long segundos) {
        long min = segundos / 60;
        long sec = segundos % 60;
        return String.format("%02d:%02d", min, sec);
    }

    private void configurarEventos() {
        jButton1.addActionListener(e -> playSong());
        jButton2.addActionListener(e -> pauseSong());
        jButton4.addActionListener(e -> previousSong());
        jButton5.addActionListener(e -> nextSong());
        jSlider1.addChangeListener(e -> {
    if (clip != null && clip.isOpen() && jSlider1.getValueIsAdjusting()) {
        long microTotal = clip.getMicrosecondLength();
        int sliderValue = jSlider1.getValue();
        long newMicroPos = (microTotal * sliderValue) / 100; // porcentaje
        clip.setMicrosecondPosition(newMicroPos);
        if (!clip.isRunning() && !paused) {
            clip.start();
        }
    }
});

    }
private void iniciarBarraProgreso() {
    // Detener hilo anterior
    if (progressThread != null && progressThread.isAlive()) {
        progressThread.interrupt();
    }

    progressThread = new Thread(() -> {
        if (clip == null) return;
        long microTotal = clip.getMicrosecondLength();

        while (clip != null && clip.isOpen()) {
    long microActual = clip.getMicrosecondPosition();
    int value = (int) ((microActual * 100) / microTotal);

    SwingUtilities.invokeLater(() -> {
        jSlider1.setValue(value);
        jLabel3.setText(formatoTiempo(microActual / 1_000_000)
                + " / " + formatoTiempo(microTotal / 1_000_000));
    });

    try { Thread.sleep(200); } catch (InterruptedException e) { break; }
}
    });

    progressThread.start();
}
    @SuppressWarnings("unchecked")
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("PLAY");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("PAUSE");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setText("<<");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText(">>");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jSlider1.setValue(0);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jButton4)
                .addGap(58, 58, 58)
                .addComponent(jButton1)
                .addGap(49, 49, 49)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addGap(38, 38, 38))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jButton4))
                .addGap(33, 33, 33))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new reproductor_mp3().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSlider jSlider1;
    // End of variables declaration//GEN-END:variables
}
