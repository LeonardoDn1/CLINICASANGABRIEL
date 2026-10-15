package presentacion.menu;

import entidades.Rol;
import javax.swing.JMenu;
import java.util.ArrayList;
import java.util.List;

public class ModuloMenu extends ComponenteMenu {

    private final JMenu menuPadre;
    private final List<ComponenteMenu> hijos = new ArrayList<>();

    public ModuloMenu(JMenu menuPadre) {
        this.menuPadre = menuPadre;
    }

    public ModuloMenu agregar(ComponenteMenu componente) {
        hijos.add(componente);
        return this;
    }

    @Override
    public boolean aplicarPermisos(Rol rolActual) {
        boolean algunHijoVisible = false;

        for (ComponenteMenu hijo : hijos) {
            boolean visible = hijo.aplicarPermisos(rolActual);
            if (visible) {
                algunHijoVisible = true;
            }
        }

        if (menuPadre != null) {
            menuPadre.setVisible(algunHijoVisible);
            menuPadre.setEnabled(algunHijoVisible);
        }

        return algunHijoVisible;
    }
}
