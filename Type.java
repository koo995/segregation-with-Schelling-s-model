import java.awt.Color;

public enum Type { 
    HEADS("0", Color.RED),
    TAILS("#", Color.BLUE),
    EMPTY(" ", Color.WHITE);

    private String icon;
    private Color color;

    Type(String icon, Color c) {
        this.icon = icon;
        this.color = c;
    }

    public String getIcon() {
        return this.icon;
    }

    public Color getColor() {
        return this.color;
    }
} 
