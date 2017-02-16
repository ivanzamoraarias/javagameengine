package elements.com;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;



public class CollideAsDeadZone extends Collidable {

	boolean enablebyEvent;
	boolean isDeadZone;
	LinkedList<GameObj> SpawnPoints;
	LinkedList<GameObj> Players;
	HashMap<Integer, GameObj> PlayersMap;
	CollideAsDeadZone(GameObj a) {
		super(a);
		enablebyEvent=false;
		SpawnPoints= new LinkedList<>();
		Players= new LinkedList<>();
		PlayersMap= new HashMap<>();
		// TODO Auto-generated constructor stub
	}
	public void addSpawnPoints(GameObj a)
	{
		SpawnPoints.add(a);
	}
	public void addPlayers(GameObj a)
	{
		PlayersMap.put(a.ID, a);
		//Players.add(a);
	}
	public void enaleDeadZoneBehavior()
	{
		isDeadZone=true;
	}
	public void deadZoneBehavior(GameObj i)
	{
		if(this.isCollideWithOther(i))
			{
			
			if(!SpawnPoints.isEmpty())
				{
					Random rand = new Random();
					GameObj j= SpawnPoints.get(rand.nextInt(SpawnPoints.size()));
					i.position[0]= j.position[0];
					i.position[1]= j.position[1];
				}
			else
				{
					i.position[0]=(new Random().nextFloat()*
						((i.worldWidth-i.objectWidth/2)-i.objectWidth/2)+i.objectWidth/2);
				
					i.position[1]=(new Random().nextFloat()*
						((i.worldHeight-i.objectHeight/2)-i.objectHeight/2)+i.objectHeight/2);
			
				}
			}
		
	}
	public void update()
	{
		for(Integer i: PlayersMap.keySet())
		{
			deadZoneBehavior(PlayersMap.get(i));
		}
	}

}
