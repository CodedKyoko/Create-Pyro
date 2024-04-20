package org.dev.createpyro.ponder.content;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.dev.createpyro.registry.PyroParticles;

import java.util.Random;

public class GunpowderScenes {
    private static final Random random = new Random();
    public static void redButtonStoryBoard(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("red_button", "Red Button");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(5);

        scene.world.showSection(util.select.position(0, 1, 1), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(0, 1, 3), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(1, 1, 1), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(1, 1, 3), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(2, 1, 1), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(2, 1, 3), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(3, 1, 1), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(3, 1, 3), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(4, 1, 1), Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(util.select.position(4, 1, 3), Direction.DOWN);
        scene.idle(2);

        scene.overlay.showText(60)
                .text("Red Button works like normal button...")
                .placeNearTarget()
                .pointAt(new Vec3(0, 1.5, 1))
                .attachKeyFrame();
        scene.idle(70);

        scene.effects.indicateRedstone(new BlockPos(0, 1, 1));
        scene.world.toggleRedstonePower(util.select.fromTo(0, 1, 1, 4, 1, 1));
        scene.overlay.showControls((new InputWindowElement(
                util.vector.blockSurface(
                        new BlockPos(0, 1, 1),
                        Direction.DOWN)
                        .add(0.0, 0.2, 0.0),
                Pointing.DOWN)).rightClick(), 20);

        scene.effects.indicateRedstone(new BlockPos(0, 1, 3));
        scene.world.toggleRedstonePower(util.select.fromTo(0, 1, 3, 4, 1, 3));
        scene.overlay.showControls((new InputWindowElement(
                util.vector.blockSurface(
                        new BlockPos(0, 1, 3),
                        Direction.DOWN)
                        .add(0.0, 0.2, 0.0),
                Pointing.DOWN)).rightClick(), 20);
        scene.idle(20);

        scene.world.toggleRedstonePower(util.select.fromTo(0, 1, 1, 4, 1, 1));
        scene.world.toggleRedstonePower(util.select.fromTo(0, 1, 3, 4, 1, 3));
        scene.idle(5);

        scene.world.hideSection(util.select.fromTo(0, 1, 1, 4, 1, 1), Direction.UP);
        scene.world.hideSection(util.select.fromTo(0, 1, 3, 4, 1, 3), Direction.UP);
        scene.idle(10);

        scene.world.showSection(util.select.fromTo(0, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(5);

        scene.overlay.showText(60)
                .text("...but it also can ignite Gunpowder Wire.")
                .placeNearTarget().pointAt(new Vec3(0, 1.5, 2))
                .attachKeyFrame();
        scene.idle(70);

        scene.effects.indicateRedstone(new BlockPos(0, 1, 2));
        scene.world.toggleRedstonePower(util.select.position(0, 1, 2));
        scene.overlay.showControls((new InputWindowElement(
                util.vector.blockSurface(new BlockPos(0, 1, 2), Direction.DOWN).add(0.0, 0.2, 0.0),
                Pointing.DOWN)).rightClick(), 20);

        for (int i = 0; i < 18; i++) {
            if (i == 4){
                scene.world.toggleRedstonePower(util.select.position(0, 1, 2));
            }
            if (i == 5){
                scene.world.setBlock(
                        new BlockPos(1, 1, 2),
                        Blocks.AIR.defaultBlockState(),
                        false);
            }
            if (i == 12){
                scene.world.setBlock(
                        new BlockPos(2, 1, 2),
                        Blocks.AIR.defaultBlockState(),
                        false);
            }
            scene.idle(5);
            Vec3 pos = new Vec3(1 + (double) (i * 3) / 18, 1.2, 2.5);
            scene.effects.emitParticles(
                    pos,
                    EmitParticlesInstruction.Emitter.simple(
                            PyroParticles.SPARK_PARTICLES.get(),
                            new Vec3(
                                    random.nextFloat(-0.1F, 0.1F),
                                    0.1,
                                    random.nextFloat(-0.1F, 0.1F)
                            )
                    ),
                    1, 2);
            scene.effects.emitParticles(
                    pos,
                    EmitParticlesInstruction.Emitter.simple(
                            ParticleTypes.ASH,
                            new Vec3(
                                    random.nextFloat(-0.1F, 0.1F),
                                    0.1,
                                    random.nextFloat(-0.1F, 0.1F)
                            )
                    ),
                    1, 2);
            if (random.nextInt(5) == 0){
                scene.effects.emitParticles(
                        pos,
                        EmitParticlesInstruction.Emitter.simple(
                                ParticleTypes.LAVA,
                                new Vec3(
                                        random.nextFloat(-0.1F, 0.1F),
                                        0.1,
                                        random.nextFloat(-0.1F, 0.1F)
                                )
                        ),
                        1, 1);
            }
        }

        scene.world.setBlock(
                new BlockPos(3, 1, 2),
                Blocks.AIR.defaultBlockState(),
                false);
        scene.world.setBlock(new BlockPos(4, 1, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world.createEntity(w -> {
            PrimedTnt tnt = EntityType.TNT.create(w);
            assert tnt != null;
            Vec3 pos = util.vector.topOf(4, 0, 2);
            tnt.setPos(pos);
            Vec3 velocity = new Vec3(
                    random.nextFloat(-0.1F, 0.1F),
                    0.1F,
                    random.nextFloat(-0.1F, 0.1F)
            );
            tnt.addDeltaMovement(velocity);
            return tnt;
        });
    }
}
