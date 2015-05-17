package sx.lambda.voxel.world;

import sx.lambda.voxel.entity.Entity;
import sx.lambda.voxel.entity.EntityPosition;
import sx.lambda.voxel.util.Vec3i;
import sx.lambda.voxel.world.chunk.IChunk;
import sx.lambda.voxel.world.generation.ChunkGenerator;

import java.util.List;

public interface IWorld {
	
	public int getChunkSize();

    public int getHeight();

    public IChunk getChunkAtPosition(Vec3i position);

    public void render();

    public void loadChunks(EntityPosition playerPosition, int viewDistance);

    public int getSeaLevel();

    /**
     * @return Gravity of the world in m/(s^2)
     */
    public float getGravity();

    /**
     *
     * @param velocity Velocity to modify, in m/s
     * @param ms Time elapsed in MS since last gravity application
     * @return Velocity affected by gravity
     */
    public float applyGravity(float velocity, long ms);

    public void removeBlock(Vec3i Vec3i);

    public void addBlock(int block, Vec3i position);

    public IChunk[] getChunksInRange(EntityPosition pos, int viewDistance);

    public void addChunk(IChunk chunk);

    public void gcChunks(EntityPosition playerPos, int viewDistance);

    public List<Entity> getLoadedEntities();

    public void addEntity(Entity e);

    void rerenderChunk(IChunk c);

    int getChunkPosition(float value);

    ChunkGenerator getChunkGen();

    void addToSunlightQueue(Vec3i block);
    void addToSunlightRemovalQueue(Vec3i block);

    void processLightQueue();

    float getLightLevel(Vec3i pos);

    void cleanup();
}