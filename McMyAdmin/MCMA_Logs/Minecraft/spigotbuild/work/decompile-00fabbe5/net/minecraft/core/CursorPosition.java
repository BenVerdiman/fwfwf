package net.minecraft.core;

public class CursorPosition {

    public static final int TYPE_INSIDE = 0;
    public static final int TYPE_FACE = 1;
    public static final int TYPE_EDGE = 2;
    public static final int TYPE_CORNER = 3;
    private final int originX;
    private final int originY;
    private final int originZ;
    private final int width;
    private final int height;
    private final int depth;
    private final int end;
    private int index;
    private int x;
    private int y;
    private int z;

    public CursorPosition(int i, int j, int k, int l, int i1, int j1) {
        this.originX = i;
        this.originY = j;
        this.originZ = k;
        this.width = l - i + 1;
        this.height = i1 - j + 1;
        this.depth = j1 - k + 1;
        this.end = this.width * this.height * this.depth;
    }

    public boolean a() {
        if (this.index == this.end) {
            return false;
        } else {
            this.x = this.index % this.width;
            int i = this.index / this.width;

            this.y = i % this.height;
            this.z = i / this.height;
            ++this.index;
            return true;
        }
    }

    public int b() {
        return this.originX + this.x;
    }

    public int c() {
        return this.originY + this.y;
    }

    public int d() {
        return this.originZ + this.z;
    }

    public int e() {
        int i = 0;

        if (this.x == 0 || this.x == this.width - 1) {
            ++i;
        }

        if (this.y == 0 || this.y == this.height - 1) {
            ++i;
        }

        if (this.z == 0 || this.z == this.depth - 1) {
            ++i;
        }

        return i;
    }
}
