package de.hechler.codingame.ghostinthecell;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player00Old {

    private final static boolean showLinks = false;
    private final static boolean showFactories = false;
    private final static boolean showTroops = false;
    private final static boolean showBombs = false;
    private final static boolean showLOthers = false;
    private final static boolean showAttack = false;

    public static String[] ownerChar = {"-", " ", "+"};
    public enum PHASE {INIT, PLAY};
    
    
    
    public static int[][] distances;
    public static int dist(int id1, int id2) { return distances[id1][id2]; }
    public static int dist(Factory fac1, int id2) { return distances[fac1.id][id2]; }
    public static int dist(int id1, Factory fac2) { return distances[id1][fac2.id]; }
    public static int dist(Factory fac1, Factory fac2) { return distances[fac1.id][fac2.id]; }
    

    public static class Move {
    	public String moves = "";
    	public void addMove(String move) {
    		if (!moves.isEmpty()) {
    			moves += ";";
    		}
    		moves += move;
    	}
    	public String get() {
    		if (moves.isEmpty()) {
    			return "WAIT";
    		}
    		return moves;
    	}
    }

	public static class World {
		public int tick;
		public int numFactories;
		Factory[] factories;
		Map<Integer, Troop> troops;
		Map<Integer, Bomb> bombs;
		public Move move;
		
    	public PHASE phase;
		
		public World(int numFactories) {
			this.tick = 0;
			this.numFactories = numFactories; 
			this.factories = new Factory[numFactories];
			this.troops = new HashMap<>();
			this.bombs = new HashMap<>();
			this.move = new Move();
			this.phase = PHASE.INIT;
		}
		public void nextTick() {
			tick += 1;
			move = new Move();
		}
		public Factory getFactoy(int facId) {
			return factories[facId];
		}
		public Troop getTroop(int troopId) {
			return troops.get(troopId);
		}
		public Troop getBomb(int bombId) {
			return troops.get(bombId);
		}
		public void updateFactory(int facId, int owner, int numCyb, int productivity, int paused) {
			Factory fac = factories[facId];
			if (fac == null) {
				initFactory(facId, owner, numCyb, productivity, paused);
			}
			else {
				fac.updateFactory(owner, numCyb, productivity, paused);
			}
		}
		public void initFactory(int facId, int owner, int numCyb, int productivity, int paused) {
			factories[facId] = new Factory(facId, owner, numCyb, productivity, paused);
		}
		public void updateTroop(int troopId, int owner, int from, int to, int numCyb, int eta) {
			Troop troop = troops.get(troopId);
			if (troop == null) {
				initTroop(troopId, owner, from, to, numCyb, eta);
			}
			else {
				troop.updateTroop(owner, from, to, numCyb, eta);
			}
		}
		private void initTroop(int troopId, int owner, int from, int to, int numCyb, int eta) {
			Troop troop = new Troop(this, troopId, owner, from, to, numCyb, eta);
			troops.put(troopId, troop);
		}
		public void updateBomb(World world, int bombId, int owner, int from, int to, int eta) {
			Bomb bomb = bombs.get(bombId);
			if (bomb == null) {
				bomb = new Bomb(this, bombId, owner, from, to, eta);
				bombs.put(bombId, bomb);
			}
		}
		public void cleanup() {
			List<Bomb> bombs2delete = new ArrayList<>();
			for (Bomb bomb:bombs.values()) {
				if (!bomb.doesExist()) {
					bombs2delete.add(bomb);
				}
			}
			List<Troop> troops2delete = new ArrayList<>();
			for (Troop troop:troops.values()) {
				if (!troop.doesExist()) {
					troops2delete.add(troop);
				}
			}
			for (Bomb bomb:bombs2delete) {
				removeBomb(bomb);
			}
			for (Troop troop:troops2delete) {
				removeTroop(troop);
			}
		}
		private void removeBomb(Bomb bomb) {
			bomb.destroy(this);
			bombs.remove(bomb.id);
		}
		private void removeTroop(Troop troop) {
			troop.destroy(this);
			troops.remove(troop.id);
		}
		public void calcEffectiveValues() {
			for (Factory fac:factories) {
				fac.resetCalculatedValues();
			}
			for (Troop troop:troops.values()) {
				Factory toFac = getFactoy(troop.to);
				if (troop.isMine()) {
					toFac.calcIncomingOwn += troop.numCyb;
				}
				else {
					toFac.calcIncomingEnemy += troop.numCyb;
				}
			}
			for (Factory fac:factories) {
				if (fac.isMine()) {
					fac.calcEffectiveNumCyb = Math.min(fac.numCyb, fac.numCyb + fac.calcIncomingOwn - fac.calcIncomingEnemy);
				}
				else {
					fac.calcEffectiveNumCyb = fac.numCyb + fac.calcIncomingEnemy - fac.calcIncomingOwn;
				}
			}
		}
		public void calcBestMoves() {
			switch (phase) {
			case INIT: {
				calcBestInitMoves();
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + phase);
			}
		}
		
		private void calcBestInitMoves() {
			List<Factory> closestFacs = getClosestFactories(0);
			System.err.println("closestFacs: "+closestFacs);
			int freeTroops = calcSumFreeTroops();
			System.err.println("freeTroops: "+freeTroops);
			for (Factory fac:closestFacs) {
				if (fac.productivity == 0) {
					continue;
				}
				if ((fac.calcEffectiveNumCyb >= 0) && (fac.calcEffectiveNumCyb < freeTroops)) {
					freeTroops = freeTroops - fac.calcEffectiveNumCyb - 1; 
					sendClosestTroops(fac, fac.calcEffectiveNumCyb+1);
				}
			}
		}
		
		private void sendClosestTroops(Factory targetFac, int num) {
			System.err.println("SCT("+targetFac+", "+num+")");
			List<Factory> closestOwnFacs = findClosestOwnFactories(targetFac); 
			for (Factory ownFac:closestOwnFacs) {
				System.err.println(ownFac);
				if (ownFac.calcEffectiveNumCyb > 0) {
					if (ownFac.calcEffectiveNumCyb >= num) {
						System.err.println("SEND ALL ");
						move.addMove("MOVE "+ownFac.id+" "+targetFac.id+" "+num);
						ownFac.numCyb -= num;
						ownFac.calcEffectiveNumCyb -= num;
						targetFac.calcIncomingOwn += num;
						return;
					}
					System.err.println("SEND PART ");
					move.addMove("MOVE "+ownFac.id+" "+targetFac.id+" "+ownFac.calcEffectiveNumCyb);
					ownFac.numCyb -= num;
					ownFac.calcEffectiveNumCyb -= ownFac.calcEffectiveNumCyb;
					targetFac.calcIncomingOwn += ownFac.calcEffectiveNumCyb;
				}
			}
		}
		

		private List<Factory> findClosestOwnFactories(Factory srcFac) {
			return findClosestFactories(srcFac.id, 1);
		}

		private List<Factory> findClosestFactories(int srcFacId, int owner) {
			List<WeightedFactory> result = new ArrayList<>(); 
			for (Factory fac:factories) {
				if (fac.owner == owner) {
					result.add(new WeightedFactory(fac, dist(srcFacId, fac)));
				}
			}
			Collections.sort(result);
			List<Factory> facResult = result.stream().map(wf -> wf.fac).collect(Collectors.toList()); 
			return facResult;
		}

		private List<Factory> findMyFactories() {
			List<Factory> result = new ArrayList<>();
			for (Factory fac:factories) {
				if (fac.isMine()) {
					result.add(fac);
				}
			}
			return result;
		}
		
		private int calcSumFreeTroops() {
			int sumFreeTroops = 0;
			for (Factory myFac:factories) {
				if (myFac.isMine()) {
					sumFreeTroops += myFac.calcEffectiveNumCyb;
				}
			}
			return sumFreeTroops;
		}

		public static class WeightedFactory implements Comparable<WeightedFactory> {
			public Factory fac;
			public int weight;
			public WeightedFactory(Factory fac, int weight) {
				this.fac = fac;
				this.weight = weight;
			}
			@Override public int compareTo(WeightedFactory otherWF) {
				return Integer.compare(weight, otherWF.weight);
			}
			
		}
		
		private List<Factory> getClosestFactories(int owner) {
			List<WeightedFactory> result = new ArrayList<>(); 
			for (Factory fac:factories) {
				if (fac.owner == owner) {
					int weight = 0;
					for (Factory myFac:factories) {
						if (myFac.isMine()) {
							weight += dist(myFac, fac);
						}
					}
					result.add(new WeightedFactory(fac, weight));
				}
			}
			Collections.sort(result);
			List<Factory> facResult = result.stream().map(wf -> wf.fac).collect(Collectors.toList()); 
			return facResult;
		}
	}

    public static abstract class WorldEntity {
        public int id;
        public int owner;
        public WorldEntity (int id, int owner) {
        	this.id = id;
        	this.owner = owner;
        }
        public boolean isMine() { return owner == 1; }
        public boolean isOpponent() { return owner == -1; }
        public boolean isNeutral() { return owner == 0; }
        public abstract void nextTick(World world);
    }

    public static class Factory extends WorldEntity {
        public int productivity;
        public int numCyb;
        public int paused;
        
        public int calcIncomingOwn;
        public int calcIncomingEnemy;
        public int calcEffectiveNumCyb;
        
        public Factory(int id, int owner, int numCyb, int productivity, int paused) { 
        	super(id, owner);
			this.owner = owner;
			this.numCyb = numCyb;
			this.productivity = productivity;
			this.paused = paused;
        }
        public void resetCalculatedValues() {
            calcIncomingOwn = 0;
            calcIncomingEnemy = 0;
            calcEffectiveNumCyb = 0;
		}
		public void updateFactory(int newOwner, int newNumCyb, int newProductivity, int newPaused) {
			owner = newOwner;
			numCyb = newNumCyb;
			productivity = newProductivity;
			paused = newPaused;
			
		}
		public void nextTick(World world) { 
        }
        @Override public String toString() {
            String addText = "";
            if (owner == 1) {
                if (calcIncomingOwn>0) {
                	addText = "+"+calcIncomingOwn;
                }
                if (calcIncomingEnemy>0) {
                	addText = "-"+calcIncomingEnemy;
                }
                if (calcEffectiveNumCyb>0) {
                	addText = "="+calcEffectiveNumCyb;
                }
            }
            else {
                if (calcIncomingEnemy>0) {
                	addText = "+"+calcIncomingEnemy;
                }
                if (calcIncomingOwn>0) {
                	addText = "-"+calcIncomingOwn;
                }
                if (calcEffectiveNumCyb>0) {
                	addText = "="+calcEffectiveNumCyb;
                }
            }
            return "F["+id+"|"+ownerChar[owner+1]+":p"+productivity+",#"+numCyb+addText+"]";
        }
    }
    
    public static abstract class MovingWorldEntity extends WorldEntity {
    	public int from;
    	public int to;
    	public int startTick;
    	public int arrivalTick;
    	public boolean exists;
        public MovingWorldEntity(World world, int id, int owner, int from, int to, int eta) {
        	super(id, owner);
        	this.startTick = world.tick;
        	this.arrivalTick = (eta == -1) ? -1 : startTick + eta; 
        	this.from = from;
        	this.to = to;
        	this.exists = true;
        }
        public void nextTick(World world) { exists = false; }
        public void update(int owner, int from, int to, int eta) { exists = true; }
        public boolean doesExist() { return exists; }
    }
    
    public static class Bomb extends MovingWorldEntity {
        public Bomb(World world, int id, int owner, int launchFactory,int  targetFactory, int eta) {
        	super(world, id, owner, launchFactory, targetFactory, eta);
        }
        public void destroy(World world) {
		}
		public void updateBomb(int owner, int from, int to, int numCyb, int eta) {
        	super.update(owner, from, to, eta);
		}
        @Override public void nextTick(World world) {
        	super.nextTick(world);
        };
        @Override public String toString() {
            return "B["+id+"|"+ownerChar[owner+1]+","+from+"->"+to+",eta="+arrivalTick+"]";
        }
    }
	
    public static class Troop extends MovingWorldEntity {
    	public int numCyb;
        public Troop(World world, int id, int owner, int launchFactory,int  targetFactory, int numCyb, int eta) {
        	super(world, id, owner, launchFactory, targetFactory, eta);
        	this.numCyb = numCyb;
        }
        public void destroy(World world) {
			// TODO Auto-generated method stub
			
		}
		public void updateTroop(int owner, int from, int to, int numCyb, int eta) {
        	super.update(owner, from, to, eta);
		}
        @Override public void nextTick(World world) {
        	super.nextTick(world);
        };
        @Override public String toString() {
            return "T["+id+"|"+ownerChar[owner+1]+","+from+"->"+to+",eta="+arrivalTick+"]";
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int factoryCount = in.nextInt(); // the number of factories
        distances = new int[factoryCount][factoryCount];
        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
            distances[factory1][factory2] = distance;
            distances[factory2][factory1] = distance;
            if (showLinks) {
                System.err.println("LINK: "+factory1+"->"+factory2+" ("+distance+")");
            }
        }

        World world = new World(factoryCount);
        // game loop
        while (true) {

            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();

                if (entityType.equals("FACTORY")) {
                	// owner, numCyb, productivity, paused
                    world.updateFactory(entityId, arg1, arg2, arg3, arg4);
                    if (showFactories) {
                        System.err.println(world.getFactoy(entityId));
                    }
                }
                else if (entityType.equals("TROOP"))  { 
                	// owner, from, to, numCyb, eta
                	world.updateTroop(entityId, arg1, arg2, arg3, arg4, arg5);
                    if (showTroops) {
                        System.err.println(world.getTroop(entityId));
                    }
                }
                else if (entityType.equals("BOMB"))  { 
                    // owner, src, dest, eta
                	world.updateBomb(world, entityId, arg1, arg2, arg3, arg4);
                    if (showBombs) {
                        System.err.println(world.getBomb(entityId));
                    }
                }
                else { 
                    if (showLOthers) {
                        System.err.println(entityType+"->"+entityId+" ("+arg1+","+arg2+","+arg3+","+arg4+","+arg5+")");
                    }
                }
            }

            // remove unreferences objects.
            world.cleanup();

            world.calcEffectiveValues();
            
            world.calcBestMoves();
            
            // output move
            System.out.println(world.move.get());
            world.nextTick();
        }
    }


    public static void sortDistance(List<Factory> result, int srcId) {
        Collections.sort(result, (f1, f2) -> {
            return Integer.compare(dist(srcId, f1), dist(srcId, f2));
        });
	}



}