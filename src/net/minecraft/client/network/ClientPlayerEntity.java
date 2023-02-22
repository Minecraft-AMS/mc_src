/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.client.sound.AmbientSoundPlayer;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.MinecartSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientPlayerEntity
extends AbstractClientPlayerEntity {
    public final ClientPlayNetworkHandler networkHandler;
    private final StatHandler statHandler;
    private final ClientRecipeBook recipeBook;
    private final List<ClientPlayerTickable> tickables = Lists.newArrayList();
    private int clientPermissionLevel = 0;
    private double lastX;
    private double lastBaseY;
    private double lastZ;
    private float lastYaw;
    private float lastPitch;
    private boolean lastOnGround;
    private boolean lastIsHoldingSneakKey;
    private boolean lastSprinting;
    private int field_3923;
    private boolean field_3918;
    private String serverBrand;
    public Input input;
    protected final MinecraftClient client;
    protected int field_3935;
    public int field_3921;
    public float renderYaw;
    public float renderPitch;
    public float lastRenderYaw;
    public float lastRenderPitch;
    private int field_3938;
    private float field_3922;
    public float nextNauseaStrength;
    public float lastNauseaStrength;
    private boolean field_3915;
    private Hand activeHand;
    private boolean riding;
    private boolean lastAutoJump = true;
    private int field_3934;
    private boolean field_3939;
    private int field_3917;

    public ClientPlayerEntity(MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler clientPlayNetworkHandler, StatHandler statHandler, ClientRecipeBook clientRecipeBook) {
        super(clientWorld, clientPlayNetworkHandler.getProfile());
        this.networkHandler = clientPlayNetworkHandler;
        this.statHandler = statHandler;
        this.recipeBook = clientRecipeBook;
        this.client = client;
        this.dimension = DimensionType.OVERWORLD;
        this.tickables.add(new AmbientSoundPlayer(this, client.getSoundManager()));
        this.tickables.add(new BubbleColumnSoundPlayer(this));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void heal(float amount) {
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        if (!super.startRiding(entity, force)) {
            return false;
        }
        if (entity instanceof AbstractMinecartEntity) {
            this.client.getSoundManager().play(new MinecartSoundInstance(this, (AbstractMinecartEntity)entity));
        }
        if (entity instanceof BoatEntity) {
            this.prevYaw = entity.yaw;
            this.yaw = entity.yaw;
            this.setHeadYaw(entity.yaw);
        }
        return true;
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        this.riding = false;
    }

    @Override
    public float getPitch(float tickDelta) {
        return this.pitch;
    }

    @Override
    public float getYaw(float tickDelta) {
        if (this.hasVehicle()) {
            return super.getYaw(tickDelta);
        }
        return this.yaw;
    }

    @Override
    public void tick() {
        if (!this.world.isBlockLoaded(new BlockPos(this.x, 0.0, this.z))) {
            return;
        }
        super.tick();
        if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
            Entity entity = this.getRootVehicle();
            if (entity != this && entity.isLogicalSideForUpdatingMovement()) {
                this.networkHandler.sendPacket(new VehicleMoveC2SPacket(entity));
            }
        } else {
            this.sendMovementPackets();
        }
        for (ClientPlayerTickable clientPlayerTickable : this.tickables) {
            clientPlayerTickable.tick();
        }
    }

    private void sendMovementPackets() {
        boolean bl2;
        boolean bl = this.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
            this.lastSprinting = bl;
        }
        if ((bl2 = this.isHoldingSneakKey()) != this.lastIsHoldingSneakKey) {
            ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.START_SNEAKING : ClientCommandC2SPacket.Mode.STOP_SNEAKING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
            this.lastIsHoldingSneakKey = bl2;
        }
        if (this.isCamera()) {
            boolean bl4;
            Box box = this.getBoundingBox();
            double d = this.x - this.lastX;
            double e = box.y1 - this.lastBaseY;
            double f = this.z - this.lastZ;
            double g = this.yaw - this.lastYaw;
            double h = this.pitch - this.lastPitch;
            ++this.field_3923;
            boolean bl3 = d * d + e * e + f * f > 9.0E-4 || this.field_3923 >= 20;
            boolean bl5 = bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(vec3d.x, -999.0, vec3d.z, this.yaw, this.pitch, this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(this.x, box.y1, this.z, this.yaw, this.pitch, this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(this.x, box.y1, this.z, this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
            }
            if (bl3) {
                this.lastX = this.x;
                this.lastBaseY = box.y1;
                this.lastZ = this.z;
                this.field_3923 = 0;
            }
            if (bl4) {
                this.lastYaw = this.yaw;
                this.lastPitch = this.pitch;
            }
            this.lastOnGround = this.onGround;
            this.lastAutoJump = this.client.options.autoJump;
        }
    }

    @Override
    @Nullable
    public ItemEntity dropSelectedItem(boolean dropEntireStack) {
        PlayerActionC2SPacket.Action action = dropEntireStack ? PlayerActionC2SPacket.Action.DROP_ALL_ITEMS : PlayerActionC2SPacket.Action.DROP_ITEM;
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
        this.inventory.takeInvStack(this.inventory.selectedSlot, dropEntireStack && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1);
        return null;
    }

    public void sendChatMessage(String string) {
        this.networkHandler.sendPacket(new ChatMessageC2SPacket(string));
    }

    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);
        this.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
    }

    @Override
    public void requestRespawn() {
        this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return;
        }
        this.setHealth(this.getHealth() - amount);
    }

    @Override
    public void closeContainer() {
        this.networkHandler.sendPacket(new GuiCloseC2SPacket(this.container.syncId));
        this.closeScreen();
    }

    public void closeScreen() {
        this.inventory.setCursorStack(ItemStack.EMPTY);
        super.closeContainer();
        this.client.openScreen(null);
    }

    public void updateHealth(float f) {
        if (this.field_3918) {
            float g = this.getHealth() - f;
            if (g <= 0.0f) {
                this.setHealth(f);
                if (g < 0.0f) {
                    this.timeUntilRegen = 10;
                }
            } else {
                this.field_6253 = g;
                this.setHealth(this.getHealth());
                this.timeUntilRegen = 20;
                this.applyDamage(DamageSource.GENERIC, g);
                this.hurtTime = this.field_6254 = 10;
            }
        } else {
            this.setHealth(f);
            this.field_3918 = true;
        }
    }

    @Override
    public void sendAbilitiesUpdate() {
        this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.abilities));
    }

    @Override
    public boolean isMainPlayer() {
        return true;
    }

    protected void startRidingJump() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_RIDING_JUMP, MathHelper.floor(this.method_3151() * 100.0f)));
    }

    public void openRidingInventory() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
    }

    public void setServerBrand(String serverBrand) {
        this.serverBrand = serverBrand;
    }

    public String getServerBrand() {
        return this.serverBrand;
    }

    public StatHandler getStatHandler() {
        return this.statHandler;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void onRecipeDisplayed(Recipe<?> recipe) {
        if (this.recipeBook.shouldDisplay(recipe)) {
            this.recipeBook.onRecipeDisplayed(recipe);
            this.networkHandler.sendPacket(new RecipeBookDataC2SPacket(recipe));
        }
    }

    @Override
    protected int getPermissionLevel() {
        return this.clientPermissionLevel;
    }

    public void setClientPermissionLevel(int i) {
        this.clientPermissionLevel = i;
    }

    @Override
    public void addChatMessage(Text message, boolean bl) {
        if (bl) {
            this.client.inGameHud.setOverlayMessage(message, false);
        } else {
            this.client.inGameHud.getChatHud().addMessage(message);
        }
    }

    @Override
    protected void pushOutOfBlocks(double x, double y, double z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (this.cannotFitAt(blockPos)) {
            double d = x - (double)blockPos.getX();
            double e = z - (double)blockPos.getZ();
            Direction direction = null;
            double f = 9999.0;
            if (!this.cannotFitAt(blockPos.west()) && d < f) {
                f = d;
                direction = Direction.WEST;
            }
            if (!this.cannotFitAt(blockPos.east()) && 1.0 - d < f) {
                f = 1.0 - d;
                direction = Direction.EAST;
            }
            if (!this.cannotFitAt(blockPos.north()) && e < f) {
                f = e;
                direction = Direction.NORTH;
            }
            if (!this.cannotFitAt(blockPos.south()) && 1.0 - e < f) {
                f = 1.0 - e;
                direction = Direction.SOUTH;
            }
            if (direction != null) {
                Vec3d vec3d = this.getVelocity();
                switch (direction) {
                    case WEST: {
                        this.setVelocity(-0.1, vec3d.y, vec3d.z);
                        break;
                    }
                    case EAST: {
                        this.setVelocity(0.1, vec3d.y, vec3d.z);
                        break;
                    }
                    case NORTH: {
                        this.setVelocity(vec3d.x, vec3d.y, -0.1);
                        break;
                    }
                    case SOUTH: {
                        this.setVelocity(vec3d.x, vec3d.y, 0.1);
                    }
                }
            }
        }
    }

    private boolean cannotFitAt(BlockPos pos) {
        Box box = this.getBoundingBox();
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
        for (int i = MathHelper.floor(box.y1); i < MathHelper.ceil(box.y2); ++i) {
            mutable.setY(i);
            if (this.doesNotSuffocate(mutable)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.field_3921 = 0;
    }

    public void method_3145(float f, int i, int j) {
        this.experienceProgress = f;
        this.totalExperience = i;
        this.experienceLevel = j;
    }

    @Override
    public void sendMessage(Text message) {
        this.client.inGameHud.getChatHud().addMessage(message);
    }

    @Override
    public void handleStatus(byte status) {
        if (status >= 24 && status <= 28) {
            this.setClientPermissionLevel(status - 24);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        this.world.playSound(this.x, this.y, this.z, sound, this.getSoundCategory(), volume, pitch, false);
    }

    @Override
    public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
        this.world.playSound(this.x, this.y, this.z, event, category, volume, pitch, false);
    }

    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }

    @Override
    public void setCurrentHand(Hand hand) {
        ItemStack itemStack = this.getStackInHand(hand);
        if (itemStack.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.setCurrentHand(hand);
        this.field_3915 = true;
        this.activeHand = hand;
    }

    @Override
    public boolean isUsingItem() {
        return this.field_3915;
    }

    @Override
    public void clearActiveItem() {
        super.clearActiveItem();
        this.field_3915 = false;
    }

    @Override
    public Hand getActiveHand() {
        return this.activeHand;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (LIVING_FLAGS.equals(data)) {
            Hand hand;
            boolean bl = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
            Hand hand2 = hand = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (bl && !this.field_3915) {
                this.setCurrentHand(hand);
            } else if (!bl && this.field_3915) {
                this.clearActiveItem();
            }
        }
        if (FLAGS.equals(data) && this.isFallFlying() && !this.field_3939) {
            this.client.getSoundManager().play(new ElytraSoundInstance(this));
        }
    }

    public boolean hasJumpingMount() {
        Entity entity = this.getVehicle();
        return this.hasVehicle() && entity instanceof JumpingMount && ((JumpingMount)((Object)entity)).canJump();
    }

    public float method_3151() {
        return this.field_3922;
    }

    @Override
    public void openEditSignScreen(SignBlockEntity signBlockEntity) {
        this.client.openScreen(new SignEditScreen(signBlockEntity));
    }

    @Override
    public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
        this.client.openScreen(new MinecartCommandBlockScreen(commandBlockExecutor));
    }

    @Override
    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
        this.client.openScreen(new CommandBlockScreen(commandBlockBlockEntity));
    }

    @Override
    public void openStructureBlockScreen(StructureBlockBlockEntity structureBlockBlockEntity) {
        this.client.openScreen(new StructureBlockScreen(structureBlockBlockEntity));
    }

    @Override
    public void openJigsawScreen(JigsawBlockEntity jigsawBlockEntity) {
        this.client.openScreen(new JigsawBlockScreen(jigsawBlockEntity));
    }

    @Override
    public void openEditBookScreen(ItemStack book, Hand hand) {
        Item item = book.getItem();
        if (item == Items.WRITABLE_BOOK) {
            this.client.openScreen(new BookEditScreen(this, book, hand));
        }
    }

    @Override
    public void addCritParticles(Entity target) {
        this.client.particleManager.addEmitter(target, ParticleTypes.CRIT);
    }

    @Override
    public void addEnchantedHitParticles(Entity target) {
        this.client.particleManager.addEmitter(target, ParticleTypes.ENCHANTED_HIT);
    }

    @Override
    public boolean isSneaking() {
        return this.isHoldingSneakKey();
    }

    public boolean isHoldingSneakKey() {
        return this.input != null && this.input.sneaking;
    }

    @Override
    public boolean isInSneakingPose() {
        if (this.abilities.flying || this.isSwimming() || !this.wouldPoseNotCollide(EntityPose.SNEAKING)) {
            return false;
        }
        return this.isHoldingSneakKey() || !this.wouldPoseNotCollide(EntityPose.STANDING);
    }

    @Override
    public void tickNewAi() {
        super.tickNewAi();
        if (this.isCamera()) {
            this.sidewaysSpeed = this.input.movementSideways;
            this.forwardSpeed = this.input.movementForward;
            this.jumping = this.input.jumping;
            this.lastRenderYaw = this.renderYaw;
            this.lastRenderPitch = this.renderPitch;
            this.renderPitch = (float)((double)this.renderPitch + (double)(this.pitch - this.renderPitch) * 0.5);
            this.renderYaw = (float)((double)this.renderYaw + (double)(this.yaw - this.renderYaw) * 0.5);
        }
    }

    protected boolean isCamera() {
        return this.client.getCameraEntity() == this;
    }

    @Override
    public void tickMovement() {
        int i;
        ItemStack itemStack;
        boolean bl6;
        ++this.field_3921;
        if (this.field_3935 > 0) {
            --this.field_3935;
        }
        this.updateNausea();
        boolean bl = this.input.jumping;
        boolean bl2 = this.input.sneaking;
        boolean bl3 = this.method_20623();
        boolean bl4 = this.isInSneakingPose() || this.shouldLeaveSwimmingPose();
        this.input.tick(bl4, this.isSpectator());
        this.client.getTutorialManager().onMovement(this.input);
        if (this.isUsingItem() && !this.hasVehicle()) {
            this.input.movementSideways *= 0.2f;
            this.input.movementForward *= 0.2f;
            this.field_3935 = 0;
        }
        boolean bl5 = false;
        if (this.field_3934 > 0) {
            --this.field_3934;
            bl5 = true;
            this.input.jumping = true;
        }
        if (!this.noClip) {
            Box box = this.getBoundingBox();
            this.pushOutOfBlocks(this.x - (double)this.getWidth() * 0.35, box.y1 + 0.5, this.z + (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.x - (double)this.getWidth() * 0.35, box.y1 + 0.5, this.z - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.x + (double)this.getWidth() * 0.35, box.y1 + 0.5, this.z - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.x + (double)this.getWidth() * 0.35, box.y1 + 0.5, this.z + (double)this.getWidth() * 0.35);
        }
        boolean bl7 = bl6 = (float)this.getHungerManager().getFoodLevel() > 6.0f || this.abilities.allowFlying;
        if (!(!this.onGround && !this.isSubmergedInWater() || bl2 || bl3 || !this.method_20623() || this.isSprinting() || !bl6 || this.isUsingItem() || this.hasStatusEffect(StatusEffects.BLINDNESS))) {
            if (this.field_3935 > 0 || this.client.options.keySprint.isPressed()) {
                this.setSprinting(true);
            } else {
                this.field_3935 = 7;
            }
        }
        if (!this.isSprinting() && (!this.isTouchingWater() || this.isSubmergedInWater()) && this.method_20623() && bl6 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && this.client.options.keySprint.isPressed()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            boolean bl8;
            boolean bl72 = !this.input.method_20622() || !bl6;
            boolean bl9 = bl8 = bl72 || this.horizontalCollision || this.isTouchingWater() && !this.isSubmergedInWater();
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.sneaking && bl72 || !this.isTouchingWater()) {
                    this.setSprinting(false);
                }
            } else if (bl8) {
                this.setSprinting(false);
            }
        }
        if (this.abilities.allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!this.abilities.flying) {
                    this.abilities.flying = true;
                    this.sendAbilitiesUpdate();
                }
            } else if (!bl && this.input.jumping && !bl5) {
                if (this.field_7489 == 0) {
                    this.field_7489 = 7;
                } else if (!this.isSwimming()) {
                    this.abilities.flying = !this.abilities.flying;
                    this.sendAbilitiesUpdate();
                    this.field_7489 = 0;
                }
            }
        }
        if (this.input.jumping && !bl && !this.onGround && this.getVelocity().y < 0.0 && !this.isFallFlying() && !this.abilities.flying && (itemStack = this.getEquippedStack(EquipmentSlot.CHEST)).getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack)) {
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
        this.field_3939 = this.isFallFlying();
        if (this.isTouchingWater() && this.input.sneaking) {
            this.method_6093();
        }
        if (this.isInFluid(FluidTags.WATER)) {
            i = this.isSpectator() ? 10 : 1;
            this.field_3917 = MathHelper.clamp(this.field_3917 + i, 0, 600);
        } else if (this.field_3917 > 0) {
            this.isInFluid(FluidTags.WATER);
            this.field_3917 = MathHelper.clamp(this.field_3917 - 10, 0, 600);
        }
        if (this.abilities.flying && this.isCamera()) {
            i = 0;
            if (this.input.sneaking) {
                this.input.movementSideways = (float)((double)this.input.movementSideways / 0.3);
                this.input.movementForward = (float)((double)this.input.movementForward / 0.3);
                --i;
            }
            if (this.input.jumping) {
                ++i;
            }
            if (i != 0) {
                this.setVelocity(this.getVelocity().add(0.0, (float)i * this.abilities.getFlySpeed() * 3.0f, 0.0));
            }
        }
        if (this.hasJumpingMount()) {
            JumpingMount jumpingMount = (JumpingMount)((Object)this.getVehicle());
            if (this.field_3938 < 0) {
                ++this.field_3938;
                if (this.field_3938 == 0) {
                    this.field_3922 = 0.0f;
                }
            }
            if (bl && !this.input.jumping) {
                this.field_3938 = -10;
                jumpingMount.setJumpStrength(MathHelper.floor(this.method_3151() * 100.0f));
                this.startRidingJump();
            } else if (!bl && this.input.jumping) {
                this.field_3938 = 0;
                this.field_3922 = 0.0f;
            } else if (bl) {
                ++this.field_3938;
                this.field_3922 = this.field_3938 < 10 ? (float)this.field_3938 * 0.1f : 0.8f + 2.0f / (float)(this.field_3938 - 9) * 0.1f;
            }
        } else {
            this.field_3922 = 0.0f;
        }
        super.tickMovement();
        if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
            this.abilities.flying = false;
            this.sendAbilitiesUpdate();
        }
    }

    private void updateNausea() {
        this.lastNauseaStrength = this.nextNauseaStrength;
        if (this.inNetherPortal) {
            if (this.client.currentScreen != null && !this.client.currentScreen.isPauseScreen()) {
                if (this.client.currentScreen instanceof ContainerScreen) {
                    this.closeContainer();
                }
                this.client.openScreen(null);
            }
            if (this.nextNauseaStrength == 0.0f) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PORTAL_TRIGGER, this.random.nextFloat() * 0.4f + 0.8f));
            }
            this.nextNauseaStrength += 0.0125f;
            if (this.nextNauseaStrength >= 1.0f) {
                this.nextNauseaStrength = 1.0f;
            }
            this.inNetherPortal = false;
        } else if (this.hasStatusEffect(StatusEffects.NAUSEA) && this.getStatusEffect(StatusEffects.NAUSEA).getDuration() > 60) {
            this.nextNauseaStrength += 0.006666667f;
            if (this.nextNauseaStrength > 1.0f) {
                this.nextNauseaStrength = 1.0f;
            }
        } else {
            if (this.nextNauseaStrength > 0.0f) {
                this.nextNauseaStrength -= 0.05f;
            }
            if (this.nextNauseaStrength < 0.0f) {
                this.nextNauseaStrength = 0.0f;
            }
        }
        this.tickNetherPortalCooldown();
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.riding = false;
        if (this.getVehicle() instanceof BoatEntity) {
            BoatEntity boatEntity = (BoatEntity)this.getVehicle();
            boatEntity.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
            this.riding |= this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack;
        }
    }

    public boolean isRiding() {
        return this.riding;
    }

    @Override
    @Nullable
    public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect type) {
        if (type == StatusEffects.NAUSEA) {
            this.lastNauseaStrength = 0.0f;
            this.nextNauseaStrength = 0.0f;
        }
        return super.removeStatusEffectInternal(type);
    }

    @Override
    public void move(MovementType type, Vec3d movement) {
        double d = this.x;
        double e = this.z;
        super.move(type, movement);
        this.method_3148((float)(this.x - d), (float)(this.z - e));
    }

    public boolean getLastAutoJump() {
        return this.lastAutoJump;
    }

    protected void method_3148(float f, float g) {
        float m;
        float j;
        if (!this.getLastAutoJump()) {
            return;
        }
        if (this.field_3934 > 0 || !this.onGround || this.isSneaking() || this.hasVehicle()) {
            return;
        }
        Vec2f vec2f = this.input.getMovementInput();
        if (vec2f.x == 0.0f && vec2f.y == 0.0f) {
            return;
        }
        Vec3d vec3d = new Vec3d(this.x, this.getBoundingBox().y1, this.z);
        Vec3d vec3d2 = new Vec3d(this.x + (double)f, this.getBoundingBox().y1, this.z + (double)g);
        Vec3d vec3d3 = new Vec3d(f, 0.0, g);
        float h = this.getMovementSpeed();
        float i = (float)vec3d3.lengthSquared();
        if (i <= 0.001f) {
            j = h * vec2f.x;
            float k = h * vec2f.y;
            float l = MathHelper.sin(this.yaw * ((float)Math.PI / 180));
            m = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
            vec3d3 = new Vec3d(j * m - k * l, vec3d3.y, k * m + j * l);
            i = (float)vec3d3.lengthSquared();
            if (i <= 0.001f) {
                return;
            }
        }
        j = (float)MathHelper.fastInverseSqrt(i);
        Vec3d vec3d4 = vec3d3.multiply(j);
        Vec3d vec3d5 = this.getRotationVecClient();
        m = (float)(vec3d5.x * vec3d4.x + vec3d5.z * vec3d4.z);
        if (m < -0.15f) {
            return;
        }
        EntityContext entityContext = EntityContext.of(this);
        BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().y2, this.z);
        BlockState blockState = this.world.getBlockState(blockPos);
        if (!blockState.getCollisionShape(this.world, blockPos, entityContext).isEmpty()) {
            return;
        }
        BlockState blockState2 = this.world.getBlockState(blockPos = blockPos.up());
        if (!blockState2.getCollisionShape(this.world, blockPos, entityContext).isEmpty()) {
            return;
        }
        float n = 7.0f;
        float o = 1.2f;
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            o += (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75f;
        }
        float p = Math.max(h * 7.0f, 1.0f / j);
        Vec3d vec3d6 = vec3d;
        Vec3d vec3d7 = vec3d2.add(vec3d4.multiply(p));
        float q = this.getWidth();
        float r = this.getHeight();
        Box box = new Box(vec3d6, vec3d7.add(0.0, r, 0.0)).expand(q, 0.0, q);
        vec3d6 = vec3d6.add(0.0, 0.51f, 0.0);
        vec3d7 = vec3d7.add(0.0, 0.51f, 0.0);
        Vec3d vec3d8 = vec3d4.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        Vec3d vec3d9 = vec3d8.multiply(q * 0.5f);
        Vec3d vec3d10 = vec3d6.subtract(vec3d9);
        Vec3d vec3d11 = vec3d7.subtract(vec3d9);
        Vec3d vec3d12 = vec3d6.add(vec3d9);
        Vec3d vec3d13 = vec3d7.add(vec3d9);
        Iterator iterator = this.world.getCollisions(this, box, Collections.emptySet()).flatMap(voxelShape -> voxelShape.getBoundingBoxes().stream()).iterator();
        float s = Float.MIN_VALUE;
        while (iterator.hasNext()) {
            Box box2 = (Box)iterator.next();
            if (!box2.intersects(vec3d10, vec3d11) && !box2.intersects(vec3d12, vec3d13)) continue;
            s = (float)box2.y2;
            Vec3d vec3d14 = box2.getCenter();
            BlockPos blockPos2 = new BlockPos(vec3d14);
            int t = 1;
            while ((float)t < o) {
                BlockState blockState4;
                BlockPos blockPos3 = blockPos2.up(t);
                BlockState blockState3 = this.world.getBlockState(blockPos3);
                VoxelShape voxelShape2 = blockState3.getCollisionShape(this.world, blockPos3, entityContext);
                if (!voxelShape2.isEmpty() && (double)(s = (float)voxelShape2.getMaximum(Direction.Axis.Y) + (float)blockPos3.getY()) - this.getBoundingBox().y1 > (double)o) {
                    return;
                }
                if (t > 1 && !(blockState4 = this.world.getBlockState(blockPos = blockPos.up())).getCollisionShape(this.world, blockPos, entityContext).isEmpty()) {
                    return;
                }
                ++t;
            }
            break block0;
        }
        if (s == Float.MIN_VALUE) {
            return;
        }
        float u = (float)((double)s - this.getBoundingBox().y1);
        if (u <= 0.5f || u > o) {
            return;
        }
        this.field_3934 = 1;
    }

    private boolean method_20623() {
        double d = 0.8;
        return this.isSubmergedInWater() ? this.input.method_20622() : (double)this.input.movementForward >= 0.8;
    }

    public float method_3140() {
        if (!this.isInFluid(FluidTags.WATER)) {
            return 0.0f;
        }
        float f = 600.0f;
        float g = 100.0f;
        if ((float)this.field_3917 >= 600.0f) {
            return 1.0f;
        }
        float h = MathHelper.clamp((float)this.field_3917 / 100.0f, 0.0f, 1.0f);
        float i = (float)this.field_3917 < 100.0f ? 0.0f : MathHelper.clamp(((float)this.field_3917 - 100.0f) / 500.0f, 0.0f, 1.0f);
        return h * 0.6f + i * 0.39999998f;
    }

    @Override
    public boolean isSubmergedInWater() {
        return this.isSubmergedInWater;
    }

    @Override
    protected boolean updateWaterSubmersionState() {
        boolean bl = this.isSubmergedInWater;
        boolean bl2 = super.updateWaterSubmersionState();
        if (this.isSpectator()) {
            return this.isSubmergedInWater;
        }
        if (!bl && bl2) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
            this.client.getSoundManager().play(new AmbientSoundLoops.Underwater(this));
        }
        if (bl && !bl2) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.isSubmergedInWater;
    }
}

