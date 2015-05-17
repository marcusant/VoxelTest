package sx.lambda.voxel.tasks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import groovy.transform.CompileStatic
import sx.lambda.voxel.VoxelGameClient
import sx.lambda.voxel.api.BuiltInBlockIds
import sx.lambda.voxel.block.Block
import sx.lambda.voxel.util.Vec3i
import sx.lambda.voxel.world.IWorld
import sx.lambda.voxel.entity.EntityPosition
import sx.lambda.voxel.entity.LivingEntity
import sx.lambda.voxel.entity.player.Player
import sx.lambda.voxel.world.chunk.IChunk

@CompileStatic
class MovementHandler implements RepeatedTask {

    private final VoxelGameClient game

    public MovementHandler(VoxelGameClient game) {
        this.game = game
    }

    @Override
    String getIdentifier() {
        return "Movement Handler"
    }

    @Override
    void run() {
        try {
            long lastMoveCheckMS = System.currentTimeMillis()
            while (!game.isDone()) {
                if (game.world == null || game.player == null) {
                    sleep(1000)
                    lastMoveCheckMS = System.currentTimeMillis()
                } else {
                    Player player = game.getPlayer()
                    IWorld world = game.getWorld()
                    long moveDiffMS = lastMoveCheckMS - System.currentTimeMillis()
                    float movementMultiplier = moveDiffMS * 0.0045
                    final boolean threeDMove = false;
                    EntityPosition lastPosition = player.getPosition().clone()
                    if (Gdx.input.isKeyPressed(Keys.W)) { // Forward TODO Config - Make keys configurable
                        float yaw = player.getRotation().getYaw()
                        float pitch = player.getRotation().getPitch()
                        float deltaX
                        float deltaY
                        float deltaZ
                        if (threeDMove) {
                            deltaX = (float) (-Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)) * movementMultiplier)
                            deltaY = (float) (-Math.sin(Math.toRadians(pitch)) * movementMultiplier)
                            deltaZ = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)) * movementMultiplier)
                        } else {
                            deltaX = (float) (-Math.sin(Math.toRadians(yaw)) * movementMultiplier)
                            deltaZ = (float) (Math.cos(Math.toRadians(yaw)) * movementMultiplier)
                            deltaY = 0
                        }

                        if (!checkCollision(player, deltaX, deltaY, deltaZ)) {
                            player.getPosition().offset(deltaX, deltaY, deltaZ)
                        }
                    }
                    if (Gdx.input.isKeyPressed(Keys.S)) {
                        float yaw = player.getRotation().getYaw()
                        float pitch = player.getRotation().getPitch()
                        float deltaX
                        float deltaY
                        float deltaZ
                        if (threeDMove) {
                            deltaX = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)) * movementMultiplier)
                            deltaY = (float) (Math.sin(Math.toRadians(pitch)) * movementMultiplier)
                            deltaZ = (float) (-Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)) * movementMultiplier)
                        } else {
                            deltaX = (float) (Math.sin(Math.toRadians(yaw)) * movementMultiplier)
                            deltaZ = (float) (-Math.cos(Math.toRadians(yaw)) * movementMultiplier)
                            deltaY = 0
                        }

                        if (!checkCollision(player, deltaX, deltaY, deltaZ)) {
                            player.getPosition().offset(deltaX, deltaY, deltaZ)
                        }
                    }
                    if (Gdx.input.isKeyPressed(Keys.A)) { //Strafe left
                        float deltaX
                        float deltaZ
                        float yaw = player.getRotation().getYaw()

                        deltaX = (float) (-Math.sin(Math.toRadians(yaw - 90)) * movementMultiplier)
                        deltaZ = (float) (Math.cos(Math.toRadians(yaw - 90)) * movementMultiplier)

                        if (!checkCollision(player, deltaX, 0, deltaZ)) {
                            player.getPosition().offset(deltaX, 0, deltaZ)
                        }
                    }
                    if (Gdx.input.isKeyPressed(Keys.D)) { //Strafe right
                        float deltaX
                        float deltaZ
                        float yaw = player.getRotation().getYaw()

                        deltaX = (float) (-Math.sin(Math.toRadians(yaw + 90)) * movementMultiplier)
                        deltaZ = (float) (Math.cos(Math.toRadians(yaw + 90)) * movementMultiplier)

                        if (!checkCollision(player, deltaX, 0, deltaZ)) {
                            player.getPosition().offset(deltaX, 0, deltaZ)
                        }
                    }

                    if (world != null && player != null) {
                        Vec3i playerPosition = new Vec3i(
                                (int) Math.floor(player.getPosition().getX()),
                                (int) Math.floor(player.getPosition().getY() - 0.2f),
                                (int) Math.floor(player.getPosition().getZ())
                        );
                        IChunk playerChunk = world.getChunkAtPosition(playerPosition);
                        player.setOnGround(false)
                        if (playerChunk != null) {
                            Block blockAtPlayer = playerChunk.getBlockAtPosition(playerPosition)
                            if (blockAtPlayer != null) {
                                if (blockAtPlayer.isSolid()) {
                                    player.setOnGround(true)
                                }
                            }
                        }

                        if (player.getBlockInFeet(world) == BuiltInBlockIds.WATER_ID) {
                            if (!Gdx.input.isKeyPressed(Keys.SPACE)) {
                                player.setYVelocity(-0.05f);
                            }
                        } else {
                            player.setYVelocity(world.applyGravity(player.getYVelocity(), moveDiffMS));
                        }
                        player.updateMovement(this);
                    }

                    if (!(player.position.equals(lastPosition))) {
                        player.setMoved(true);
                    }

                    lastMoveCheckMS = System.currentTimeMillis()
                    sleep(10)
                }
            }
        } catch(Exception e) {
            game.handleCriticalException(e)
        }
    }

    public boolean checkCollision(LivingEntity e, float deltaX, float deltaY, float deltaZ) {
        Vec3i newPosition = new Vec3i(
                (int) Math.floor(e.getPosition().getX() + deltaX),
                (int) Math.floor(e.getPosition().getY() - 0.1 + deltaY),
                (int) Math.floor(e.getPosition().getZ() + deltaZ)
        );
        Vec3i newPosition2 = new Vec3i(
                (int) Math.floor(e.getPosition().getX() + deltaX),
                (int) Math.floor(e.getPosition().getY() + e.getHeight() - 0.1 + deltaY),
                (int) Math.floor(e.getPosition().getZ() + deltaZ)
        );
        IChunk newChunk = game.getWorld().getChunkAtPosition(newPosition);
        IChunk newChunk2 = game.getWorld().getChunkAtPosition(newPosition2);
        if (newChunk2 == null) return true
        if (newChunk == null) return true
        Block block1 = newChunk.getBlockAtPosition(newPosition);
        Block block2 = newChunk2.getBlockAtPosition(newPosition2);

        boolean passed = true
        if(block1 != null) {
            if(block1.isSolid()) {
                passed = false
            }
        }
        if(block2 != null) {
            if(block2.isSolid()) {
                passed = false
            }
        }

        return !passed
    }

}