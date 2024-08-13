package com.besson.crazyzombie.entity;

import com.besson.crazyzombie.effects.Effects;
import com.besson.crazyzombie.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrazyZombieEntity extends ZombieEntity implements RangedAttackMob {
    private int tick_count = 0;
    private BlockPos old_pos = this.getBlockPos();
    private int block_tick_count = 0;
    private boolean can_block = false;
    private String zombie_type = null;
    private int tnt_boom_tick = 0;
    private int spider_tick = 0;
    private Boolean had_skill = false;
    private int put_block_interval = 20;
    private double ai_speed = 0.15D;
    private boolean had_release_dead_skill = false;

    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false) {
        public void stop() {
            super.stop();
        }

        public void start() {
            super.start();
        }
    };

    private static class ZombieType {
        public static final String NORMAL = "normal";
        public static final String BOWER = "bower";
        public static final String BOOMER = "boomer";
        public static final String SPIDER_MAN = "spider_man";
        public static final String BUILDER = "builder";
        public static final String RAPER = "raper";
    }

    private final BowAttackGoal bowAttackGoal = new BowAttackGoal(this, 1.0, 10, 20.0F);
    private List<BlockPos> BlockPosList = new ArrayList<>();
    private int clear_block_count = 0;

    public CrazyZombieEntity(EntityType<CrazyZombieEntity> type, World world) {
        super(EntityType.ZOMBIE, world);
        if (getDaysPassed(this.getWorld()) == 0){
            this.kill();
            return;
        }
        initModGoals();

        int zombie_type_num = (int)(Math.random() * 100);
        zombie_type = check_type(zombie_type_num);
        String custom_name = "§4ERROR";

        //debug_info
        //CrazyZombie.LOGGER.info("{},{}", zombie_type_num, zombie_type);

        if (Objects.equals(zombie_type, ZombieType.NORMAL)) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
            this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LIME_BANNER));
            custom_name = "§2§l战士僵尸";

        } else if (Objects.equals(zombie_type, ZombieType.BOWER)) {
            initEquipment(this.getWorld().getRandom(), this.getWorld().getLocalDifficulty(BlockPos.ORIGIN));
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.ARROW));
            this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GLASS));
            this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
            init();
            custom_name = "§6§l弓箭手僵尸";

        } else if (Objects.equals(zombie_type, ZombieType.BOOMER)) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FLINT_AND_STEEL));
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.TNT));
            custom_name = "§4§lTNT僵尸";

        } else if (Objects.equals(zombie_type, ZombieType.SPIDER_MAN)) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.COBWEB));
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            this.ai_speed = 0.2D;
            custom_name = "§8§l蜘蛛僵尸";

        } else if (Objects.equals(zombie_type, ZombieType.BUILDER)) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.YELLOW_STAINED_GLASS));
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.IRON_SHOVEL));
            this.put_block_interval = 5;
            this.ai_speed = 0.3D;
            custom_name = "§e§l工程师僵尸";
        } else if (Objects.equals(zombie_type, ZombieType.RAPER)) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(ModItems.RAPER_HAT));
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.MIC));
            custom_name = "§5§l说唱僵尸";
        }
        this.setCustomName(Text.of(custom_name));
    }

    @Override
    protected boolean burnsInDaylight() {
        return false;
    }

    public void initModGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        if (Objects.equals(this.zombie_type, ZombieType.NORMAL)) {
            this.goalSelector.add(1, this.bowAttackGoal);
        }
    }

    @Override
    protected void initCustomGoals() {
        if (!Objects.equals(this.zombie_type, ZombieType.BOWER)) {
            this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        }
        this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    public String check_type(int random_num) {
        if (random_num < 20 && random_num >= 0) {
            return ZombieType.BOWER;
        } else if(random_num >= 20 && random_num < 35){
            return ZombieType.BOOMER;
        } else if(random_num >= 35 && random_num < 50){
            return ZombieType.SPIDER_MAN;
        } else if (random_num >= 50 && random_num < 80) {
            return ZombieType.NORMAL;
        } else if (random_num >= 80 && random_num < 95) {
            return ZombieType.BUILDER;
        } else if (random_num >= 95 && random_num <= 100) {
            return ZombieType.RAPER;
        }
        return "";
    }

    public void Clear_block_tick() {
        if (this.clear_block_count >= 30) {
            clear_block_count = 0;
        } else {
            clear_block_count++;
            return;
        }
        if (!BlockPosList.isEmpty()) {
            BlockPos last_block_pos = BlockPosList.getFirst();
            BlockPosList.removeFirst();
            this.getWorld().setBlockState(last_block_pos, Blocks.AIR.getDefaultState());
        }
    }

    public void block_tick() {
        if (block_tick_count + 1 >= this.put_block_interval) {
            block_tick_count = 0;
        } else {
            block_tick_count ++;
            return;
        }
        can_block = true;
    }

    public void Builder_tick() {
        Entity entity = this.get_closest_by_pose(this.getPos());
        if (entity != null) {
            entity.setPosition(this.getPos());
        }
    }

    public void Spider_man_tick(final double distance, final PlayerEntity player) {
        if (!this.had_skill) {
            if (spider_tick + 1 >= 75) {
                spider_tick = 0;
                if (distance > 16) {
                    this.had_skill = true;
                    return;
                }

            } else {
                spider_tick++;
                return;
            }
        }

        if (distance > 16) {
            return;
        }

        boolean done = false;

        if (!this.isDead()) {
            player.addStatusEffect(new StatusEffectInstance(
                    Effects.PARALYSIS_EFFECT, 200, 0, true, false));
            done = true;
        }
        if (done) {
            //放一个蜘蛛网，让玩家更加难以逃脱
            this.getWorld().setBlockState(
                    player.getBlockPos(),
                    Blocks.COBWEB.getDefaultState(), 3);
            this.setPosition(player.getPos());
            this.had_skill = false;
        }
    }

    public void tnt_check_tick(final double distance) {
        if (!this.had_skill) {
            if (tnt_boom_tick + 1 >= 80) {
                tnt_boom_tick = 0;
                if (distance > 16) {
                    this.had_skill = true;
                    return;
                }

            } else {
                tnt_boom_tick++;
                return;
            }
        }

        if (distance > 16) {
            return;
        }

        Vec3d self_pos = this.getPos();
        TntEntity tnt = new TntEntity(EntityType.TNT, this.getWorld());
        this.getWorld().spawnEntity(tnt);
        this.kill();
        tnt.setPosition(self_pos);
        tnt.setFuse(0);

        this.had_skill = false;
    }

    public void init(){
        this.updateAttackType();
    }

    public void idle_check() {
        /*
        检测僵尸是不是停下来了？
        如果是,就尝试破坏它周围的方块。
        */
        if (this.tick_count + 1 >= 50) {
            this.tick_count = 0;
        } else {
            BlockPos now_block_pos = this.getBlockPos();
            if(now_block_pos != this.old_pos) {
                this.tick_count = 0;
                this.old_pos = now_block_pos;
                return;
            }
            this.tick_count++;
            return;
        }

        BlockPos self_block_pos = this.getBlockPos();
        BlockPos[] blockPosList = new BlockPos[]{
                new BlockPos(self_block_pos.add(1, 0, 1)),
                new BlockPos(self_block_pos.add(-1, 0, -1)),
                new BlockPos(self_block_pos.add(1, 0, -1)),
                new BlockPos(self_block_pos.add(-1, 0, 1)),
                new BlockPos(self_block_pos.add(0, 0, 1)),
                new BlockPos(self_block_pos.add(0, 0, -1)),
                new BlockPos(self_block_pos.add(1, 0, 0)),
                new BlockPos(self_block_pos.add(-1, 0, 0))
        };
        BlockState Air = Blocks.AIR.getDefaultState();
        World world = this.getWorld();
        for (BlockPos blockPos : blockPosList) {
            world.breakBlock(blockPos, false);
        }
        for (BlockPos blockPos : blockPosList) {
            world.breakBlock(blockPos.add(0, 1, 0), false);
        }
    }

    public Entity get_closest_by_pose(Vec3d pos){
        double minDistance = Double.MAX_VALUE;
        Entity nearestEntity = null;

        // 遍历范围中的所有实体
        for (Entity entity : this.getWorld().
                getOtherEntities(
                        this, Box.of(this.getPos(), 5, 2,5))
                )
        {
            if (!(entity instanceof ClientPlayerEntity)) { // 排除玩家自身和其他玩家
                if (entity.getClass() == CrazyZombieEntity.class) {
                    if (!Objects.equals(((CrazyZombieEntity) entity).zombie_type, ZombieType.BUILDER)) {
                        double distance = pos.distanceTo(entity.getPos());
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestEntity = entity;
                        }
                    }
                }
            }
        }

        return nearestEntity;
    }

    public static int getDaysPassed(World world) {
        long totalTicks = world.getTime(); // 获取总刻数

        // 由于一个游戏日包含2000刻，我们将总刻数除以2000得到天数

        return (int) (totalTicks / 2000L);
    }

    // 重写tick方法，这个方法在每一帧都会被调用
    @Override
    public void tick() {
        if (this.isDead() && Objects.equals(this.zombie_type, ZombieType.BOOMER) && !this.had_release_dead_skill){
            /*
            自爆僵尸死亡之后自爆
             */
            Vec3d self_pos = this.getPos();
            TntEntity tnt = new TntEntity(EntityType.TNT, this.getWorld());
            this.getWorld().spawnEntity(tnt);
            tnt.setPosition(self_pos);
            tnt.setFuse(0);
            this.had_release_dead_skill = true;
        }
        // 首先调用父类的tick方法，以确保僵尸的基本行为（如移动、攻击等）得以执行
        super.tick();

        this.idle_check();
        this.block_tick();
        this.Clear_block_tick();
        //this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 30, 4));
        // 获取距离僵尸150格范围内的最近玩家

        float except_distance = 0;
        if (Objects.equals(this.zombie_type, ZombieType.BOWER)){
            except_distance = 36.0F;
        } else {
            except_distance = 4.0F;
        }

        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this.getX(), this.getY(), this.getZ(), 150.0F, true);

        if (nearestPlayer != null) {
            double moveX = nearestPlayer.getX() - this.getX();
            double moveZ = nearestPlayer.getZ() - this.getZ();
            double moveY = nearestPlayer.getY() - this.getY();
            double distance = moveX * moveX + moveZ * moveZ;
            World world = this.getWorld();

            //根据种类不同对应技能
            if (Objects.equals(this.zombie_type, ZombieType.BOOMER)) {
                this.tnt_check_tick(distance);

            } else if(Objects.equals(this.zombie_type, ZombieType.SPIDER_MAN)){
                this.Spider_man_tick(distance, nearestPlayer);

            } else if (Objects.equals(this.zombie_type, ZombieType.BUILDER)) {
                this.Builder_tick();
            }

            // 如果僵尸的Y轴位置低于玩家，并且差值大于一定阈值
            double v = (Math.atan2(moveZ, moveX) * (180.0 / Math.PI)) - 90.0;

            if (moveY > 0.5) {
                // 停止直接移动
                this.setVelocity(0.0, this.getVelocity().y, 0.0);

                if (this.can_block){
                    // 尝试跳跃
                    if (this.isOnGround()) {
                        this.setPosition(this.getPos().add(0.0F, 1.0F, 0.0F));
                    }

                    // 在僵尸下方放置泥土块以达到玩家的Y轴位置
                    BlockPos posBelow = this.getBlockPos().add(0, -1, 0);
                    BlockPos posBelow2 = this.getBlockPos().add(0, 1, 0);
                    world.setBlockState(posBelow, Blocks.DIRT.getDefaultState(), 3);
                    world.breakBlock(posBelow2, false);

                    this.BlockPosList.add(posBelow);
                    this.can_block = false;
                }

            } else if (moveY < -0.5) {
                this.setVelocity(0.0, this.getVelocity().y , 0.0);

                if (this.can_block) {

                    if (this.isOnGround() && !this.jumping) {
                        // 在僵尸下方破坏方块以达到玩家的Y轴位置
                        BlockPos posBelow = this.getBlockPos().add(0, -1, 0);

                        world.breakBlock(posBelow, false);
                    }
                    this.can_block = false;
                }

            } else {
                // 如果僵尸的Y轴位置与玩家相近于玩家，则正常移动
                if (distance > except_distance) {
                    Vec3d moveVector = new Vec3d(moveX, 0.0D, moveZ).normalize().multiply(this.ai_speed);
                    this.move(MovementType.SELF, moveVector);

                    // 更新朝向
                    this.setRotation((float) v, 0.1F);
                }
                // 检查是否“踩空”，如果是则在其脚下放置泥土块
                BlockPos posBelow = this.getBlockPos().add(0, -2, 0);
                BlockPos posBelow2 = this.getBlockPos().add(0, -1, 0);

                BlockState blockStateBelow = this.getWorld().getBlockState(posBelow);
                if (blockStateBelow.isAir() && this.getWorld().getBlockState(posBelow2).isAir()) {
                    this.getWorld().setBlockState(posBelow2, Blocks.DIRT.getDefaultState(), 3);
                    this.BlockPosList.add(posBelow2);
                }
            }
        }
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        ItemStack itemStack2 = this.getProjectileType(itemStack);
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(persistentProjectileEntity);
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier, shotFrom);
    }

    public void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
    }

    public void updateAttackType() {
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.goalSelector.remove(this.meleeAttackGoal);
            this.goalSelector.remove(this.bowAttackGoal);
            ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            if (itemStack.isOf(Items.BOW)) {
                int i = this.getHardAttackInterval();
                if (this.getWorld().getDifficulty() != Difficulty.HARD) {
                    i = this.getRegularAttackInterval();
                }

                this.bowAttackGoal.setAttackInterval(i);
                this.goalSelector.add(4, this.bowAttackGoal);
            } else {
                this.goalSelector.add(4, this.meleeAttackGoal);
            }

        }
    }

    protected int getHardAttackInterval() {
        return 10;
    }

    protected int getRegularAttackInterval() {
        return 40;
    }
}