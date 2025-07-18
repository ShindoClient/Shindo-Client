package me.miki.shindo.ui.elements;

public abstract class Element {
    protected int x, y, width, height;

    public Element(int x, int y, int width, int height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public abstract void draw(int mouseX, int mouseY, float alpha);
    public abstract void mouseClicked(int mouseX, int mouseY, int button);
    public void mouseReleased(int mouseX, int mouseY, int button) {}
    public void keyTyped(char typedChar, int keyCode) {}

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setSize(int width, int height) { this.width = width; this.height = height; }
}
