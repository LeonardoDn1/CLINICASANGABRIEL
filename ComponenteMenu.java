package presentacion.menu;

import entidades.Rol;

public abstract class ComponenteMenu {

    public abstract boolean aplicarPermisos(Rol rolActual);
}
