/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.client.sound.AmbientSoundPlayer;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.MinecartInsideSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.DecoratableArgumentList;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
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
import net.minecraft.util.Arm;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CommandBlockExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientPlayerEntity
extends AbstractClientPlayerEntity {
    public static final Logger field_39078 = LogUtils.getLogger();
    private static final int field_32671 = 20;
    private static final int field_32672 = 600;
    private static final int field_32673 = 100;
    private static final float field_32674 = 0.6f;
    private static final double field_32675 = 0.35;
    private static final double MAX_SOFT_COLLISION_RADIANS = 0.13962633907794952;
    private static final float field_38337 = 0.3f;
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
    private boolean inSneakingPose;
    private boolean lastSneaking;
    private boolean lastSprinting;
    private int ticksSinceLastPositionPacketSent;
    private boolean healthInitialized;
    @Nullable
    private String serverBrand;
    public Input input;
    protected final MinecraftClient client;
    protected int ticksLeftToDoubleTapSprint;
    public int ticksSinceSprintingChanged;
    public float renderYaw;
    public float renderPitch;
    public float lastRenderYaw;
    public float lastRenderPitch;
    private int field_3938;
    private float mountJumpStrength;
    public float nextNauseaStrength;
    public float lastNauseaStrength;
    private boolean usingItem;
    @Nullable
    private Hand activeHand;
    private boolean riding;
    private boolean autoJumpEnabled = true;
    private int ticksToNextAutojump;
    private boolean falling;
    private int underwaterVisibilityTicks;
    private boolean showsDeathScreen = true;

    public ClientPlayerEntity(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
        super(world, networkHandler.getProfile(), client.getProfileKeys().getPublicKey().orElse(null));
        this.client = client;
        this.networkHandler = networkHandler;
        this.statHandler = stats;
        this.recipeBook = recipeBook;
        this.lastSneaking = lastSneaking;
        this.lastSprinting = lastSprinting;
        this.tickables.add(new AmbientSoundPlayer(this, client.getSoundManager()));
        this.tickables.add(new BubbleColumnSoundPlayer(this));
        this.tickables.add(new BiomeEffectSoundPlayer(this, client.getSoundManager(), world.getBiomeAccess()));
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
            this.client.getSoundManager().play(new MinecartInsideSoundInstance(this, (AbstractMinecartEntity)entity, true));
            this.client.getSoundManager().play(new MinecartInsideSoundInstance(this, (AbstractMinecartEntity)entity, false));
        }
        return true;
    }

    @Override
    public void dismountVehicle() {
        super.dismountVehicle();
        this.riding = false;
    }

    @Override
    public float getPitch(float tickDelta) {
        return this.getPitch();
    }

    @Override
    public float getYaw(float tickDelta) {
        if (this.hasVehicle()) {
            return super.getYaw(tickDelta);
        }
        return this.getYaw();
    }

    @Override
    public void tick() {
        if (!this.world.isPosLoaded(this.getBlockX(), this.getBlockZ())) {
            return;
        }
        super.tick();
        if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
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

    public float getMoodPercentage() {
        for (ClientPlayerTickable clientPlayerTickable : this.tickables) {
            if (!(clientPlayerTickable instanceof BiomeEffectSoundPlayer)) continue;
            return ((BiomeEffectSoundPlayer)clientPlayerTickable).getMoodPercentage();
        }
        return 0.0f;
    }

    private void sendMovementPackets() {
        boolean bl2;
        boolean bl = this.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
            this.lastSprinting = bl;
        }
        if ((bl2 = this.isSneaking()) != this.lastSneaking) {
            ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
            this.lastSneaking = bl2;
        }
        if (this.isCamera()) {
            boolean bl4;
            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = this.getYaw() - this.lastYaw;
            double h = this.getPitch() - this.lastPitch;
            ++this.ticksSinceLastPositionPacketSent;
            boolean bl3 = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl5 = bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, this.getYaw(), this.getPitch(), this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(this.onGround));
            }
            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl4) {
                this.lastYaw = this.getYaw();
                this.lastPitch = this.getPitch();
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
        }
    }

    public boolean dropSelectedItem(boolean entireStack) {
        PlayerActionC2SPacket.Action action = entireStack ? PlayerActionC2SPacket.Action.DROP_ALL_ITEMS : PlayerActionC2SPacket.Action.DROP_ITEM;
        ItemStack itemStack = this.getInventory().dropSelectedItem(entireStack);
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
        return !itemStack.isEmpty();
    }

    public void sendChatMessage(String message, @Nullable Text preview) {
        this.sendChatMessageInternal(message, preview);
    }

    public boolean hasSignedArgument(String command) {
        ParseResults parseResults = this.networkHandler.getCommandDispatcher().parse(command, (Object)this.networkHandler.getCommandSource());
        return ArgumentSignatureDataMap.hasSignedArgument(DecoratableArgumentList.of(parseResults));
    }

    public boolean sendCommand(String command) {
        if (!this.hasSignedArgument(command)) {
            LastSeenMessageList.Acknowledgment acknowledgment = this.networkHandler.consumeAcknowledgment();
            this.networkHandler.sendPacket(new CommandExecutionC2SPacket(command, Instant.now(), 0L, ArgumentSignatureDataMap.EMPTY, false, acknowledgment));
            return true;
        }
        return false;
    }

    public void sendCommand(String command, @Nullable Text preview) {
        this.sendCommandInternal(command, preview);
    }

    private void sendChatMessageInternal(String message, @Nullable Text preview) {
        DecoratedContents decoratedContents = this.toDecoratedContents(message, preview);
        MessageMetadata messageMetadata = this.createMessageMetadata();
        LastSeenMessageList.Acknowledgment acknowledgment = this.networkHandler.consumeAcknowledgment();
        MessageSignatureData messageSignatureData = this.signChatMessage(messageMetadata, decoratedContents, acknowledgment.lastSeen());
        this.networkHandler.sendPacket(new ChatMessageC2SPacket(decoratedContents.plain(), messageMetadata.timestamp(), messageMetadata.salt(), messageSignatureData, decoratedContents.isDecorated(), acknowledgment));
    }

    private MessageSignatureData signChatMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages) {
        try {
            Signer signer = this.client.getProfileKeys().getSigner();
            if (signer != null) {
                return this.networkHandler.getMessagePacker().pack(signer, metadata, content, lastSeenMessages).signature();
            }
        }
        catch (Exception exception) {
            field_39078.error("Failed to sign chat message: '{}'", (Object)content.plain(), (Object)exception);
        }
        return MessageSignatureData.EMPTY;
    }

    private void sendCommandInternal(String command, @Nullable Text preview) {
        ParseResults parseResults = this.networkHandler.getCommandDispatcher().parse(command, (Object)this.networkHandler.getCommandSource());
        MessageMetadata messageMetadata = this.createMessageMetadata();
        LastSeenMessageList.Acknowledgment acknowledgment = this.networkHandler.consumeAcknowledgment();
        ArgumentSignatureDataMap argumentSignatureDataMap = this.signArguments(messageMetadata, (ParseResults<CommandSource>)parseResults, preview, acknowledgment.lastSeen());
        this.networkHandler.sendPacket(new CommandExecutionC2SPacket(command, messageMetadata.timestamp(), messageMetadata.salt(), argumentSignatureDataMap, preview != null, acknowledgment));
    }

    private ArgumentSignatureDataMap signArguments(MessageMetadata signer, ParseResults<CommandSource> parseResults, @Nullable Text preview, LastSeenMessageList lastSeenMessages) {
        Signer signer2 = this.client.getProfileKeys().getSigner();
        if (signer2 == null) {
            return ArgumentSignatureDataMap.EMPTY;
        }
        try {
            return ArgumentSignatureDataMap.sign(DecoratableArgumentList.of(parseResults), (argumentName, value) -> {
                DecoratedContents decoratedContents = this.toDecoratedContents(value, preview);
                return this.networkHandler.getMessagePacker().pack(signer2, signer, decoratedContents, lastSeenMessages).signature();
            });
        }
        catch (Exception exception) {
            field_39078.error("Failed to sign command arguments", (Throwable)exception);
            return ArgumentSignatureDataMap.EMPTY;
        }
    }

    private DecoratedContents toDecoratedContents(String message, @Nullable Text preview) {
        String string = StringHelper.truncateChat(message);
        if (preview != null) {
            return new DecoratedContents(string, preview);
        }
        return new DecoratedContents(string);
    }

    private MessageMetadata createMessageMetadata() {
        return MessageMetadata.of(this.getUuid());
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
    public void closeHandledScreen() {
        this.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(this.currentScreenHandler.syncId));
        this.closeScreen();
    }

    public void closeScreen() {
        super.closeHandledScreen();
        this.client.setScreen(null);
    }

    public void updateHealth(float health) {
        if (this.healthInitialized) {
            float f = this.getHealth() - health;
            if (f <= 0.0f) {
                this.setHealth(health);
                if (f < 0.0f) {
                    this.timeUntilRegen = 10;
                }
            } else {
                this.lastDamageTaken = f;
                this.timeUntilRegen = 20;
                this.setHealth(health);
                this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.healthInitialized = true;
        }
    }

    @Override
    public void sendAbilitiesUpdate() {
        this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.getAbilities()));
    }

    @Override
    public boolean isMainPlayer() {
        return true;
    }

    @Override
    public boolean isHoldingOntoLadder() {
        return !this.getAbilities().flying && super.isHoldingOntoLadder();
    }

    @Override
    public boolean shouldSpawnSprintingParticles() {
        return !this.getAbilities().flying && super.shouldSpawnSprintingParticles();
    }

    @Override
    public boolean shouldDisplaySoulSpeedEffects() {
        return !this.getAbilities().flying && super.shouldDisplaySoulSpeedEffects();
    }

    protected void startRidingJump() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_RIDING_JUMP, MathHelper.floor(this.getMountJumpStrength() * 100.0f)));
    }

    public void openRidingInventory() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
    }

    public void setServerBrand(@Nullable String serverBrand) {
        this.serverBrand = serverBrand;
    }

    @Nullable
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

    public void setClientPermissionLevel(int clientPermissionLevel) {
        this.clientPermissionLevel = clientPermissionLevel;
    }

    @Override
    public void sendMessage(Text message, boolean overlay) {
        this.client.getMessageHandler().onGameMessage(message, overlay);
    }

    private void pushOutOfBlocks(double x, double z) {
        Direction[] directions;
        BlockPos blockPos = new BlockPos(x, this.getY(), z);
        if (!this.wouldCollideAt(blockPos)) {
            return;
        }
        double d = x - (double)blockPos.getX();
        double e = z - (double)blockPos.getZ();
        Direction direction = null;
        double f = Double.MAX_VALUE;
        for (Direction direction2 : directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
            double h;
            double g = direction2.getAxis().choose(d, 0.0, e);
            double d2 = h = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - g : g;
            if (!(h < f) || this.wouldCollideAt(blockPos.offset(direction2))) continue;
            f = h;
            direction = direction2;
        }
        if (direction != null) {
            Vec3d vec3d = this.getVelocity();
            if (direction.getAxis() == Direction.Axis.X) {
                this.setVelocity(0.1 * (double)direction.getOffsetX(), vec3d.y, vec3d.z);
            } else {
                this.setVelocity(vec3d.x, vec3d.y, 0.1 * (double)direction.getOffsetZ());
            }
        }
    }

    private boolean wouldCollideAt(BlockPos pos) {
        Box box = this.getBoundingBox();
        Box box2 = new Box(pos.getX(), box.minY, pos.getZ(), (double)pos.getX() + 1.0, box.maxY, (double)pos.getZ() + 1.0).contract(1.0E-7);
        return this.world.canCollide(this, box2);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.ticksSinceSprintingChanged = 0;
    }

    public void setExperience(float progress, int total, int level) {
        this.experienceProgress = progress;
        this.totalExperience = total;
        this.experienceLevel = level;
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

    public void setShowsDeathScreen(boolean showsDeathScreen) {
        this.showsDeathScreen = showsDeathScreen;
    }

    public boolean showsDeathScreen() {
        return this.showsDeathScreen;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        this.world.playSound(this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch, false);
    }

    @Override
    public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
        this.world.playSound(this.getX(), this.getY(), this.getZ(), event, category, volume, pitch, false);
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
        this.usingItem = true;
        this.activeHand = hand;
    }

    @Override
    public boolean isUsingItem() {
        return this.usingItem;
    }

    @Override
    public void clearActiveItem() {
        super.clearActiveItem();
        this.usingItem = false;
    }

    @Override
    public Hand getActiveHand() {
        return Objects.requireNonNullElse(this.activeHand, Hand.MAIN_HAND);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (LIVING_FLAGS.equals(data)) {
            Hand hand;
            boolean bl = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
            Hand hand2 = hand = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (bl && !this.usingItem) {
                this.setCurrentHand(hand);
            } else if (!bl && this.usingItem) {
                this.clearActiveItem();
            }
        }
        if (FLAGS.equals(data) && this.isFallFlying() && !this.falling) {
            this.client.getSoundManager().play(new ElytraSoundInstance(this));
        }
    }

    public boolean hasJumpingMount() {
        Entity entity = this.getVehicle();
        return this.hasVehicle() && entity instanceof JumpingMount && ((JumpingMount)((Object)entity)).canJump();
    }

    public float getMountJumpStrength() {
        return this.mountJumpStrength;
    }

    @Override
    public void openEditSignScreen(SignBlockEntity sign) {
        this.client.setScreen(new SignEditScreen(sign, this.client.shouldFilterText()));
    }

    @Override
    public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
        this.client.setScreen(new MinecartCommandBlockScreen(commandBlockExecutor));
    }

    @Override
    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
        this.client.setScreen(new CommandBlockScreen(commandBlock));
    }

    @Override
    public void openStructureBlockScreen(StructureBlockBlockEntity structureBlock) {
        this.client.setScreen(new StructureBlockScreen(structureBlock));
    }

    @Override
    public void openJigsawScreen(JigsawBlockEntity jigsaw) {
        this.client.setScreen(new JigsawBlockScreen(jigsaw));
    }

    @Override
    public void useBook(ItemStack book, Hand hand) {
        if (book.isOf(Items.WRITABLE_BOOK)) {
            this.client.setScreen(new BookEditScreen(this, book, hand));
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
        return this.input != null && this.input.sneaking;
    }

    @Override
    public boolean isInSneakingPose() {
        return this.inSneakingPose;
    }

    public boolean shouldSlowDown() {
        return this.isInSneakingPose() || this.isCrawling();
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
            this.renderPitch += (this.getPitch() - this.renderPitch) * 0.5f;
            this.renderYaw += (this.getYaw() - this.renderYaw) * 0.5f;
        }
    }

    protected boolean isCamera() {
        return this.client.getCameraEntity() == this;
    }

    public void init() {
        this.setPose(EntityPose.STANDING);
        if (this.world != null) {
            for (double d = this.getY(); d > (double)this.world.getBottomY() && d < (double)this.world.getTopY(); d += 1.0) {
                this.setPosition(this.getX(), d, this.getZ());
                if (this.world.isSpaceEmpty(this)) break;
            }
            this.setVelocity(Vec3d.ZERO);
            this.setPitch(0.0f);
        }
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    @Override
    public void tickMovement() {
        int i;
        ItemStack itemStack;
        boolean bl6;
        boolean bl5;
        ++this.ticksSinceSprintingChanged;
        if (this.ticksLeftToDoubleTapSprint > 0) {
            --this.ticksLeftToDoubleTapSprint;
        }
        this.updateNausea();
        boolean bl = this.input.jumping;
        boolean bl2 = this.input.sneaking;
        boolean bl3 = this.isWalking();
        this.inSneakingPose = !this.getAbilities().flying && !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));
        float f = MathHelper.clamp(0.3f + EnchantmentHelper.getSwiftSneakSpeedBoost(this), 0.0f, 1.0f);
        this.input.tick(this.shouldSlowDown(), f);
        this.client.getTutorialManager().onMovement(this.input);
        if (this.isUsingItem() && !this.hasVehicle()) {
            this.input.movementSideways *= 0.2f;
            this.input.movementForward *= 0.2f;
            this.ticksLeftToDoubleTapSprint = 0;
        }
        boolean bl4 = false;
        if (this.ticksToNextAutojump > 0) {
            --this.ticksToNextAutojump;
            bl4 = true;
            this.input.jumping = true;
        }
        if (!this.noClip) {
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getZ() + (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getZ() - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getZ() - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getZ() + (double)this.getWidth() * 0.35);
        }
        if (bl2) {
            this.ticksLeftToDoubleTapSprint = 0;
        }
        boolean bl7 = bl5 = (float)this.getHungerManager().getFoodLevel() > 6.0f || this.getAbilities().allowFlying;
        if (!(!this.onGround && !this.isSubmergedInWater() || bl2 || bl3 || !this.isWalking() || this.isSprinting() || !bl5 || this.isUsingItem() || this.hasStatusEffect(StatusEffects.BLINDNESS))) {
            if (this.ticksLeftToDoubleTapSprint > 0 || this.client.options.sprintKey.isPressed()) {
                this.setSprinting(true);
            } else {
                this.ticksLeftToDoubleTapSprint = 7;
            }
        }
        if (!this.isSprinting() && (!this.isTouchingWater() || this.isSubmergedInWater()) && this.isWalking() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && this.client.options.sprintKey.isPressed()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            boolean bl72;
            bl6 = !this.input.hasForwardMovement() || !bl5;
            boolean bl8 = bl72 = bl6 || this.horizontalCollision && !this.collidedSoftly || this.isTouchingWater() && !this.isSubmergedInWater();
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.sneaking && bl6 || !this.isTouchingWater()) {
                    this.setSprinting(false);
                }
            } else if (bl72) {
                this.setSprinting(false);
            }
        }
        bl6 = false;
        if (this.getAbilities().allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!this.getAbilities().flying) {
                    this.getAbilities().flying = true;
                    bl6 = true;
                    this.sendAbilitiesUpdate();
                }
            } else if (!bl && this.input.jumping && !bl4) {
                if (this.abilityResyncCountdown == 0) {
                    this.abilityResyncCountdown = 7;
                } else if (!this.isSwimming()) {
                    this.getAbilities().flying = !this.getAbilities().flying;
                    bl6 = true;
                    this.sendAbilitiesUpdate();
                    this.abilityResyncCountdown = 0;
                }
            }
        }
        if (this.input.jumping && !bl6 && !bl && !this.getAbilities().flying && !this.hasVehicle() && !this.isClimbing() && (itemStack = this.getEquippedStack(EquipmentSlot.CHEST)).isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack) && this.checkFallFlying()) {
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
        this.falling = this.isFallFlying();
        if (this.isTouchingWater() && this.input.sneaking && this.shouldSwimInFluids()) {
            this.knockDownwards();
        }
        if (this.isSubmergedIn(FluidTags.WATER)) {
            i = this.isSpectator() ? 10 : 1;
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + i, 0, 600);
        } else if (this.underwaterVisibilityTicks > 0) {
            this.isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
        }
        if (this.getAbilities().flying && this.isCamera()) {
            i = 0;
            if (this.input.sneaking) {
                --i;
            }
            if (this.input.jumping) {
                ++i;
            }
            if (i != 0) {
                this.setVelocity(this.getVelocity().add(0.0, (float)i * this.getAbilities().getFlySpeed() * 3.0f, 0.0));
            }
        }
        if (this.hasJumpingMount()) {
            JumpingMount jumpingMount = (JumpingMount)((Object)this.getVehicle());
            if (this.field_3938 < 0) {
                ++this.field_3938;
                if (this.field_3938 == 0) {
                    this.mountJumpStrength = 0.0f;
                }
            }
            if (bl && !this.input.jumping) {
                this.field_3938 = -10;
                jumpingMount.setJumpStrength(MathHelper.floor(this.getMountJumpStrength() * 100.0f));
                this.startRidingJump();
            } else if (!bl && this.input.jumping) {
                this.field_3938 = 0;
                this.mountJumpStrength = 0.0f;
            } else if (bl) {
                ++this.field_3938;
                this.mountJumpStrength = this.field_3938 < 10 ? (float)this.field_3938 * 0.1f : 0.8f + 2.0f / (float)(this.field_3938 - 9) * 0.1f;
            }
        } else {
            this.mountJumpStrength = 0.0f;
        }
        super.tickMovement();
        if (this.onGround && this.getAbilities().flying && !this.client.interactionManager.isFlyingLocked()) {
            this.getAbilities().flying = false;
            this.sendAbilitiesUpdate();
        }
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void updateNausea() {
        this.lastNauseaStrength = this.nextNauseaStrength;
        if (this.inNetherPortal) {
            if (!(this.client.currentScreen == null || this.client.currentScreen.shouldPause() || this.client.currentScreen instanceof DeathScreen || this.client.currentScreen instanceof DownloadingTerrainScreen)) {
                if (this.client.currentScreen instanceof HandledScreen) {
                    this.closeHandledScreen();
                }
                this.client.setScreen(null);
            }
            if (this.nextNauseaStrength == 0.0f) {
                this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRIGGER, this.random.nextFloat() * 0.4f + 0.8f, 0.25f));
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
        this.tickPortalCooldown();
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
    public void move(MovementType movementType, Vec3d movement) {
        double d = this.getX();
        double e = this.getZ();
        super.move(movementType, movement);
        this.autoJump((float)(this.getX() - d), (float)(this.getZ() - e));
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    protected void autoJump(float dx, float dz) {
        float j;
        if (!this.shouldAutoJump()) {
            return;
        }
        Vec3d vec3d = this.getPos();
        Vec3d vec3d2 = vec3d.add(dx, 0.0, dz);
        Vec3d vec3d3 = new Vec3d(dx, 0.0, dz);
        float f = this.getMovementSpeed();
        float g = (float)vec3d3.lengthSquared();
        if (g <= 0.001f) {
            Vec2f vec2f = this.input.getMovementInput();
            float h = f * vec2f.x;
            float i = f * vec2f.y;
            j = MathHelper.sin(this.getYaw() * ((float)Math.PI / 180));
            float k = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180));
            vec3d3 = new Vec3d(h * k - i * j, vec3d3.y, i * k + h * j);
            g = (float)vec3d3.lengthSquared();
            if (g <= 0.001f) {
                return;
            }
        }
        float l = MathHelper.fastInverseSqrt(g);
        Vec3d vec3d4 = vec3d3.multiply(l);
        Vec3d vec3d5 = this.getRotationVecClient();
        j = (float)(vec3d5.x * vec3d4.x + vec3d5.z * vec3d4.z);
        if (j < -0.15f) {
            return;
        }
        ShapeContext shapeContext = ShapeContext.of(this);
        BlockPos blockPos = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
        BlockState blockState = this.world.getBlockState(blockPos);
        if (!blockState.getCollisionShape(this.world, blockPos, shapeContext).isEmpty()) {
            return;
        }
        BlockState blockState2 = this.world.getBlockState(blockPos = blockPos.up());
        if (!blockState2.getCollisionShape(this.world, blockPos, shapeContext).isEmpty()) {
            return;
        }
        float m = 7.0f;
        float n = 1.2f;
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            n += (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75f;
        }
        float o = Math.max(f * 7.0f, 1.0f / l);
        Vec3d vec3d6 = vec3d;
        Vec3d vec3d7 = vec3d2.add(vec3d4.multiply(o));
        float p = this.getWidth();
        float q = this.getHeight();
        Box box = new Box(vec3d6, vec3d7.add(0.0, q, 0.0)).expand(p, 0.0, p);
        vec3d6 = vec3d6.add(0.0, 0.51f, 0.0);
        vec3d7 = vec3d7.add(0.0, 0.51f, 0.0);
        Vec3d vec3d8 = vec3d4.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        Vec3d vec3d9 = vec3d8.multiply(p * 0.5f);
        Vec3d vec3d10 = vec3d6.subtract(vec3d9);
        Vec3d vec3d11 = vec3d7.subtract(vec3d9);
        Vec3d vec3d12 = vec3d6.add(vec3d9);
        Vec3d vec3d13 = vec3d7.add(vec3d9);
        Iterable<VoxelShape> iterable = this.world.getCollisions(this, box);
        Iterator iterator = StreamSupport.stream(iterable.spliterator(), false).flatMap(shape -> shape.getBoundingBoxes().stream()).iterator();
        float r = Float.MIN_VALUE;
        while (iterator.hasNext()) {
            Box box2 = (Box)iterator.next();
            if (!box2.intersects(vec3d10, vec3d11) && !box2.intersects(vec3d12, vec3d13)) continue;
            r = (float)box2.maxY;
            Vec3d vec3d14 = box2.getCenter();
            BlockPos blockPos2 = new BlockPos(vec3d14);
            int s = 1;
            while ((float)s < n) {
                BlockState blockState4;
                BlockPos blockPos3 = blockPos2.up(s);
                BlockState blockState3 = this.world.getBlockState(blockPos3);
                VoxelShape voxelShape = blockState3.getCollisionShape(this.world, blockPos3, shapeContext);
                if (!voxelShape.isEmpty() && (double)(r = (float)voxelShape.getMax(Direction.Axis.Y) + (float)blockPos3.getY()) - this.getY() > (double)n) {
                    return;
                }
                if (s > 1 && !(blockState4 = this.world.getBlockState(blockPos = blockPos.up())).getCollisionShape(this.world, blockPos, shapeContext).isEmpty()) {
                    return;
                }
                ++s;
            }
            break block0;
        }
        if (r == Float.MIN_VALUE) {
            return;
        }
        float t = (float)((double)r - this.getY());
        if (t <= 0.5f || t > n) {
            return;
        }
        this.ticksToNextAutojump = 1;
    }

    @Override
    protected boolean hasCollidedSoftly(Vec3d adjustedMovement) {
        float f = this.getYaw() * ((float)Math.PI / 180);
        double d = MathHelper.sin(f);
        double e = MathHelper.cos(f);
        double g = (double)this.sidewaysSpeed * e - (double)this.forwardSpeed * d;
        double h = (double)this.forwardSpeed * e + (double)this.sidewaysSpeed * d;
        double i = MathHelper.square(g) + MathHelper.square(h);
        double j = MathHelper.square(adjustedMovement.x) + MathHelper.square(adjustedMovement.z);
        if (i < (double)1.0E-5f || j < (double)1.0E-5f) {
            return false;
        }
        double k = g * adjustedMovement.x + h * adjustedMovement.z;
        double l = Math.acos(k / Math.sqrt(i * j));
        return l < 0.13962633907794952;
    }

    private boolean shouldAutoJump() {
        return this.isAutoJumpEnabled() && this.ticksToNextAutojump <= 0 && this.onGround && !this.clipAtLedge() && !this.hasVehicle() && this.hasMovementInput() && (double)this.getJumpVelocityMultiplier() >= 1.0;
    }

    private boolean hasMovementInput() {
        Vec2f vec2f = this.input.getMovementInput();
        return vec2f.x != 0.0f || vec2f.y != 0.0f;
    }

    private boolean isWalking() {
        double d = 0.8;
        return this.isSubmergedInWater() ? this.input.hasForwardMovement() : (double)this.input.movementForward >= 0.8;
    }

    public float getUnderwaterVisibility() {
        if (!this.isSubmergedIn(FluidTags.WATER)) {
            return 0.0f;
        }
        float f = 600.0f;
        float g = 100.0f;
        if ((float)this.underwaterVisibilityTicks >= 600.0f) {
            return 1.0f;
        }
        float h = MathHelper.clamp((float)this.underwaterVisibilityTicks / 100.0f, 0.0f, 1.0f);
        float i = (float)this.underwaterVisibilityTicks < 100.0f ? 0.0f : MathHelper.clamp(((float)this.underwaterVisibilityTicks - 100.0f) / 500.0f, 0.0f, 1.0f);
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
            this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
            this.client.getSoundManager().play(new AmbientSoundLoops.Underwater(this));
        }
        if (bl && !bl2) {
            this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.isSubmergedInWater;
    }

    @Override
    public Vec3d getLeashPos(float delta) {
        if (this.client.options.getPerspective().isFirstPerson()) {
            float f = MathHelper.lerp(delta * 0.5f, this.getYaw(), this.prevYaw) * ((float)Math.PI / 180);
            float g = MathHelper.lerp(delta * 0.5f, this.getPitch(), this.prevPitch) * ((float)Math.PI / 180);
            double d = this.getMainArm() == Arm.RIGHT ? -1.0 : 1.0;
            Vec3d vec3d = new Vec3d(0.39 * d, -0.6, 0.3);
            return vec3d.rotateX(-g).rotateY(-f).add(this.getCameraPosVec(delta));
        }
        return super.getLeashPos(delta);
    }

    @Override
    public void onPickupSlotClick(ItemStack cursorStack, ItemStack slotStack, ClickType clickType) {
        this.client.getTutorialManager().onPickupSlotClick(cursorStack, slotStack, clickType);
    }

    @Override
    public float getBodyYaw() {
        return this.getYaw();
    }
}

