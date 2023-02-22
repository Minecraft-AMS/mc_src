/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.minecraft.client.model;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public final class ModelPart {
    public static final float field_37937 = 1.0f;
    public float pivotX;
    public float pivotY;
    public float pivotZ;
    public float pitch;
    public float yaw;
    public float roll;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;
    public boolean visible = true;
    public boolean hidden;
    private final List<Cuboid> cuboids;
    private final Map<String, ModelPart> children;
    private ModelTransform defaultTransform = ModelTransform.NONE;

    public ModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        this.cuboids = cuboids;
        this.children = children;
    }

    public ModelTransform getTransform() {
        return ModelTransform.of(this.pivotX, this.pivotY, this.pivotZ, this.pitch, this.yaw, this.roll);
    }

    public ModelTransform getDefaultTransform() {
        return this.defaultTransform;
    }

    public void setDefaultTransform(ModelTransform transform) {
        this.defaultTransform = transform;
    }

    public void resetTransform() {
        this.setTransform(this.defaultTransform);
    }

    public void setTransform(ModelTransform rotationData) {
        this.pivotX = rotationData.pivotX;
        this.pivotY = rotationData.pivotY;
        this.pivotZ = rotationData.pivotZ;
        this.pitch = rotationData.pitch;
        this.yaw = rotationData.yaw;
        this.roll = rotationData.roll;
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.zScale = 1.0f;
    }

    public void copyTransform(ModelPart part) {
        this.xScale = part.xScale;
        this.yScale = part.yScale;
        this.zScale = part.zScale;
        this.pitch = part.pitch;
        this.yaw = part.yaw;
        this.roll = part.roll;
        this.pivotX = part.pivotX;
        this.pivotY = part.pivotY;
        this.pivotZ = part.pivotZ;
    }

    public boolean hasChild(String child) {
        return this.children.containsKey(child);
    }

    public ModelPart getChild(String name) {
        ModelPart modelPart = this.children.get(name);
        if (modelPart == null) {
            throw new NoSuchElementException("Can't find part " + name);
        }
        return modelPart;
    }

    public void setPivot(float x, float y, float z) {
        this.pivotX = x;
        this.pivotY = y;
        this.pivotZ = z;
    }

    public void setAngles(float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.render(matrices, vertices, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrices.push();
        this.rotate(matrices);
        if (!this.hidden) {
            this.renderCuboids(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);
        }
        for (ModelPart modelPart : this.children.values()) {
            modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
        matrices.pop();
    }

    public void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer) {
        this.forEachCuboid(matrices, consumer, "");
    }

    private void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer, String path) {
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrices.push();
        this.rotate(matrices);
        MatrixStack.Entry entry = matrices.peek();
        for (int i = 0; i < this.cuboids.size(); ++i) {
            consumer.accept(entry, path, i, this.cuboids.get(i));
        }
        String string = path + "/";
        this.children.forEach((name, part) -> part.forEachCuboid(matrices, consumer, string + name));
        matrices.pop();
    }

    public void rotate(MatrixStack matrices) {
        matrices.translate(this.pivotX / 16.0f, this.pivotY / 16.0f, this.pivotZ / 16.0f);
        if (this.pitch != 0.0f || this.yaw != 0.0f || this.roll != 0.0f) {
            matrices.multiply(new Quaternionf().rotationZYX(this.roll, this.yaw, this.pitch));
        }
        if (this.xScale != 1.0f || this.yScale != 1.0f || this.zScale != 1.0f) {
            matrices.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        for (Cuboid cuboid : this.cuboids) {
            cuboid.renderCuboid(entry, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }

    public Cuboid getRandomCuboid(Random random) {
        return this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    public boolean isEmpty() {
        return this.cuboids.isEmpty();
    }

    public void translate(Vector3f vec3f) {
        this.pivotX += vec3f.x();
        this.pivotY += vec3f.y();
        this.pivotZ += vec3f.z();
    }

    public void rotate(Vector3f vec3f) {
        this.pitch += vec3f.x();
        this.yaw += vec3f.y();
        this.roll += vec3f.z();
    }

    public void scale(Vector3f vec3f) {
        this.xScale += vec3f.x();
        this.yScale += vec3f.y();
        this.zScale += vec3f.z();
    }

    public Stream<ModelPart> traverse() {
        return Stream.concat(Stream.of(this), this.children.values().stream().flatMap(ModelPart::traverse));
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface CuboidConsumer {
        public void accept(MatrixStack.Entry var1, String var2, int var3, Cuboid var4);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Cuboid {
        private final Quad[] sides;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cuboid(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight) {
            this.minX = x;
            this.minY = y;
            this.minZ = z;
            this.maxX = x + sizeX;
            this.maxY = y + sizeY;
            this.maxZ = z + sizeZ;
            this.sides = new Quad[6];
            float f = x + sizeX;
            float g = y + sizeY;
            float h = z + sizeZ;
            x -= extraX;
            y -= extraY;
            z -= extraZ;
            f += extraX;
            g += extraY;
            h += extraZ;
            if (mirror) {
                float i = f;
                f = x;
                x = i;
            }
            Vertex vertex = new Vertex(x, y, z, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(f, y, z, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(f, g, z, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(x, g, z, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(x, y, h, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(f, y, h, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(f, g, h, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(x, g, h, 8.0f, 0.0f);
            float j = u;
            float k = (float)u + sizeZ;
            float l = (float)u + sizeZ + sizeX;
            float m = (float)u + sizeZ + sizeX + sizeX;
            float n = (float)u + sizeZ + sizeX + sizeZ;
            float o = (float)u + sizeZ + sizeX + sizeZ + sizeX;
            float p = v;
            float q = (float)v + sizeZ;
            float r = (float)v + sizeZ + sizeY;
            this.sides[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror, Direction.DOWN);
            this.sides[3] = new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror, Direction.UP);
            this.sides[1] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror, Direction.WEST);
            this.sides[4] = new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror, Direction.NORTH);
            this.sides[0] = new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST);
            this.sides[5] = new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror, Direction.SOUTH);
        }

        public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            for (Quad quad : this.sides) {
                Vector3f vector3f = matrix3f.transform(new Vector3f((Vector3fc)quad.direction));
                float f = vector3f.x();
                float g = vector3f.y();
                float h = vector3f.z();
                for (Vertex vertex : quad.vertices) {
                    float i = vertex.pos.x() / 16.0f;
                    float j = vertex.pos.y() / 16.0f;
                    float k = vertex.pos.z() / 16.0f;
                    Vector4f vector4f = matrix4f.transform(new Vector4f(i, j, k, 1.0f));
                    vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Quad {
        public final Vertex[] vertices;
        public final Vector3f direction;

        public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction) {
            this.vertices = vertices;
            float f = 0.0f / squishU;
            float g = 0.0f / squishV;
            vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
            vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
            vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
            vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
            if (flip) {
                int i = vertices.length;
                for (int j = 0; j < i / 2; ++j) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }
            this.direction = direction.getUnitVector();
            if (flip) {
                this.direction.mul(-1.0f, 1.0f, 1.0f);
            }
        }
    }
}

