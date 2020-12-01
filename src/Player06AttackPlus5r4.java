import java.util.*;
import java.util.stream.Collectors;

/**
 * seed=656322372
 * https://www.codingame.com/replay/512108173
 * 
 * seed=290265141
 * 
 * seed=51334912
 * seed=196039244
 * seed=814969575
 * 
 * https://www.codingame.com/share-replay/512099216
 **/
class Player06AttackPlus5r4 {

	public static LogLevel LOG_LEVEL = LogLevel.INFO;
	
    private final static boolean showLinks = false;
    private final static boolean showFactories = false;
    private final static boolean showTroops = false;
    private final static boolean showBombs = false;
    private final static boolean showLOthers = false;

    public static String[] ownerChar = {"-", " ", "+"};
	public enum LogLevel {TRACE, DEBUG, INFO, WARN, ERROR, FATAL}
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
		
		public Set<Factory> lostFactories;
		
		public List<Factory> cachedOwnFactories;
		public List<Factory> cachedEnemyFactories;
		
    	public PHASE phase;
    	
    	public int bombsLeft;
    	public Bomb lastOwnBomb;
    	public Bomb followUpBomb;
		
		public World(int numFactories) {
			this.tick = 0;
			this.numFactories = numFactories; 
			this.factories = new Factory[numFactories];
			this.troops = new HashMap<>();
			this.bombs = new HashMap<>();
			this.move = new Move();
			this.phase = PHASE.INIT;
			this.lostFactories = new HashSet<>();
			this.bombsLeft = 2;
			this.cachedOwnFactories = null;
			this.cachedEnemyFactories = null;
		}
		public void nextTick() {
			tick += 1;
			move = new Move();
			for (Factory fac:factories) {
				fac.nextTick(this);
			}
			for (Troop troop:troops.values()) {
				troop.nextTick(this);
			}
			for (Bomb bomb:bombs.values()) {
				bomb.nextTick(this);
			}
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
				fac.updateFactory(this, owner, numCyb, productivity, paused);
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
		public void updateBomb(int bombId, int owner, int from, int to, int eta) {
			Bomb bomb = bombs.get(bombId);
			if (bomb == null) {
				initBomb(bombId, owner, from, to, eta);
			}
			else {
				bomb.updateBomb(owner, from, to, eta);
			}
		}
		private void initBomb(int bombId, int owner, int from, int to, int eta) {
			Bomb bomb = new Bomb(this, bombId, owner, from, to, eta);
			bombs.put(bombId, bomb);
			if (owner == 1) {
				lastOwnBomb = bomb;
				followUpBomb = bomb;
				bombsLeft -= 1;
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
			cachedOwnFactories = null;
			cachedEnemyFactories = null;
		}
		private void removeBomb(Bomb bomb) {
			if (bomb == lastOwnBomb) {
				e("remove: ", lastOwnBomb);
				lastOwnBomb = null;
			}
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
				d("calc: ", troop, " -> ", toFac);
			}
			for (Factory fac:factories) {
				if (fac.isMine()) {
					fac.calcEffectiveNumCyb = fac.numCyb + fac.calcIncomingOwn - fac.calcIncomingEnemy;
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
			case PLAY: {
				calcBestPlayMoves();
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + phase);
			}
		}
		
		private void calcBestInitMoves() {
			sendBombs();
			List<Factory> closestFacs = getClosestFactories(0);
			d("closestFacs: ", closestFacs);
			int freeTroops = calcSumFreeTroops();
			d("freeTroops: ", freeTroops);
			boolean noNeutralFactoryLeft = true;
			for (Factory fac:closestFacs) {
				if (fac.productivity == 0) {
					continue;
				}
				noNeutralFactoryLeft = false;
				if ((fac.calcEffectiveNumCyb >= 0) && (fac.calcEffectiveNumCyb < freeTroops)) {
					int neededTroops = Math.max(fac.numCyb, fac.calcEffectiveNumCyb); 
					if (neededTroops < freeTroops) {
						freeTroops = freeTroops - neededTroops - 1; 
						sendClosestTroops(fac, neededTroops+1);
					}
				}
			}
			if (noNeutralFactoryLeft) {
				phase = PHASE.PLAY;
				move.addMove("MSG --- PHASE CHANGED TO PLAY ---");
				calcBestPlayMoves();
			}
		}
		
		private void calcBestPlayMoves() {
			sendBombs();
			List<Factory> enemyFacs = sortClosestToOwnArea(enemyFactories());
			d("enemyFacs: ", enemyFacs);
			int freeTroops = calcSumFreeTroops();
			d("freeTroops: ", freeTroops);
			for (Factory fac:enemyFacs) {
				int neededTroops = Math.max(fac.numCyb, fac.calcEffectiveNumCyb)+5;   // +5r4r1r47r10, +4R40, +6R17R7R6, +7R13
				if (neededTroops < freeTroops) {
					freeTroops = freeTroops - neededTroops - 1; 
					sendClosestTroops(fac, neededTroops+1);
				}
			}
		}
		
		private void sendBombs() {
			if (followUpBomb != null) {
				Factory fromFac = getFactoy(followUpBomb.from);
				if (fromFac.isMine() && (fromFac.numCyb > 1)) {
					Factory toFac = getFactoy(followUpBomb.to);
					move.addMove("MOVE "+fromFac.id+" "+toFac.id+" 1");
					fromFac.numCyb -= 1;
					fromFac.calcEffectiveNumCyb -= 1;
					toFac.calcIncomingOwn += 1;
				}
				followUpBomb = null;
			}
			if (bombsLeft <= 0) {
				return;
			}
			if (lastOwnBomb != null) {
				return;
			}
			List<Factory> enemyFacs = enemyFactories();
			if (enemyFacs.isEmpty()) {
				return;
			}
			Factory biggestEnemyFactory = enemyFacs.stream().max((f1, f2) -> {
				return Integer.compare(f1.numCyb, f2.numCyb);} ).get();
			if (biggestEnemyFactory.numCyb < 10) {
				return;
			}
			Factory closestOwnFac = selectClosestToFactory(biggestEnemyFactory, ownFactories());
			move.addMove("BOMB "+closestOwnFac.id+" "+biggestEnemyFactory.id);
		}
		
		public Factory selectClosestToFactory(Factory srcFac, List<Factory> possibleFacs) {
			List<WeightedFactory> result = new ArrayList<>(); 
			for (Factory fac:possibleFacs) {
				result.add(new WeightedFactory(fac, dist(srcFac, fac)));
			}
			return result.stream().max((wf1, wf2) -> {
				return wf1.compareTo(wf2);
			}).get().fac;
		}

		private List<Factory> sortClosestToOwnArea(Collection<Factory> unsortedFac) {
			List<WeightedFactory> result = new ArrayList<>(); 
			for (Factory fac:unsortedFac) {
				int weight = 0;
				for (Factory myFac:factories) {
					if (myFac == fac) {
						continue;
					}
					if (myFac.isMine()) {
						weight += dist(myFac, fac);
					}
					else if (myFac.isEnemy()) {
						weight += 20-dist(myFac, fac);
					}
					
				}
				result.add(new WeightedFactory(fac, weight));
			}
			Collections.sort(result);
			List<Factory> facResult = result.stream().map(wf -> wf.fac).collect(Collectors.toList()); 
			return facResult;
		}
		
		public List<Factory> sortClosestToFactory(Factory srcFac, List<Factory> unsortedFacs) {
			List<WeightedFactory> result = new ArrayList<>(); 
			for (Factory fac:unsortedFacs) {
				result.add(new WeightedFactory(fac, dist(srcFac, fac)));
			}
			Collections.sort(result);
			List<Factory> facResult = result.stream().map(wf -> wf.fac).collect(Collectors.toList()); 
			return facResult;
		}

		private void sendClosestTroops(Factory targetFac, int num) {
			d("SCT(", targetFac, ", ", num, ")");
			List<Factory> closestOwnFacs = findClosestOwnFactories(targetFac); 
			for (Factory ownFac:closestOwnFacs) {
				if (ownFac == targetFac) {
					continue;
				}
				d("FROM: ", ownFac);
				int numSend = Math.min(ownFac.calcEffectiveNumCyb, ownFac.numCyb);
				if (numSend > 0) {
					if (numSend >= num) {
						numSend = num;
						d("SEND ALL ");
						move.addMove("MOVE "+ownFac.id+" "+targetFac.id+" "+numSend);
						ownFac.numCyb -= numSend;
						ownFac.calcEffectiveNumCyb -= numSend;
						targetFac.calcIncomingOwn += numSend;
						return;
					}
					d("SEND PART ");
					move.addMove("MOVE "+ownFac.id+" "+targetFac.id+" "+numSend);
					ownFac.numCyb -= numSend;
					ownFac.calcEffectiveNumCyb -= numSend;
					targetFac.calcIncomingOwn += numSend;
				}
			}
		}
		

		public List<Factory> findClosestOwnFactories(Factory srcFac) {
			return sortClosestToFactory(srcFac, ownFactories());
		}
		private List<Factory> ownFactories() {
			if (cachedOwnFactories != null) {
				return cachedOwnFactories;
			}
			cachedOwnFactories = new ArrayList<>();
			for (Factory fac:factories) {
				if (fac.isMine()) {
					cachedOwnFactories.add(fac);
				}
			}
			return cachedOwnFactories;
		}
		private List<Factory> enemyFactories() {
			if (cachedEnemyFactories != null) {
				return cachedEnemyFactories;
			}
			cachedEnemyFactories = new ArrayList<>();
			for (Factory fac:factories) {
				if (fac.isEnemy()) {
					cachedEnemyFactories.add(fac);
				}
			}
			return cachedEnemyFactories;
		}
		
		private int calcSumFreeTroops() {
			int sumFreeTroops = 0;
			for (Factory myFac:factories) {
				if (myFac.isMine()) {
					sumFreeTroops += Math.max(0, Math.min(myFac.numCyb-1, myFac.calcEffectiveNumCyb-1));
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
						if (fac == myFac) {
							continue;
						}
						if (myFac.isMine()) {
							weight += dist(myFac, fac);
						}
						else if (myFac.isEnemy()) {
							weight += 20-dist(myFac, fac);
						}
						
					}
					result.add(new WeightedFactory(fac, weight));
				}
			}
			Collections.sort(result);
			List<Factory> facResult = result.stream().map(wf -> wf.fac).collect(Collectors.toList()); 
			return facResult;
		}
		public void factoryLost(Factory factory) {
			if (phase == PHASE.INIT) {
				phase = PHASE.PLAY;
				move.addMove("MSG --- PHASE CHANGED TO PLAY ---");
			}
			lostFactories.add(factory);
		}
		public void factoryWon(Factory factory) {
			lostFactories.remove(factory);
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
        public boolean isEnemy() { return owner == -1; }
        public boolean isNeutral() { return owner == 0; }
        public abstract void nextTick(World world);
		@Override
		public int hashCode() {
			return id;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WorldEntity other = (WorldEntity) obj;
			if (id != other.id)
				return false;
			return true;
		}
        
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
		public void updateFactory(World world, int newOwner, int newNumCyb, int newProductivity, int newPaused) {
			int oldOwner = owner;
			owner = newOwner;
			numCyb = newNumCyb;
			productivity = newProductivity;
			paused = newPaused;
			if (oldOwner != newOwner) {
				if (oldOwner == 1) {
					world.factoryLost(this);
				}
				else if (newOwner == 1) {
					world.factoryWon(this);
				}
			}
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
		public void updateBomb(int owner, int from, int to, int eta) {
        	super.update(owner, from, to, eta);
		}
        @Override public void nextTick(World world) {
        	super.nextTick(world);
        }
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
		}
		public void updateTroop(int owner, int from, int to, int numCyb, int eta) {
        	super.update(owner, from, to, eta);
		}
        @Override public void nextTick(World world) {
        	super.nextTick(world);
        }
        @Override public String toString() {
            return "T["+id+"|"+ownerChar[owner+1]+","+from+"->"+to+"#"+numCyb+",eta="+arrivalTick+"]";
        }
    }

    public static void main(String args[]) {
        try (Scanner in = new Scanner(System.in)) {
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
	                i("LINK: "+factory1+"->"+factory2+" ("+distance+")");
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
	                        i(world.getFactoy(entityId));
	                    }
	                }
	                else if (entityType.equals("TROOP"))  { 
	                	// owner, from, to, numCyb, eta
	                	world.updateTroop(entityId, arg1, arg2, arg3, arg4, arg5);
	                    if (showTroops) {
	                        i(world.getTroop(entityId));
	                    }
	                }
	                else if (entityType.equals("BOMB"))  { 
	                    // owner, src, dest, eta
	                	world.updateBomb(entityId, arg1, arg2, arg3, arg4);
	                    if (showBombs) {
	                        i(world.getBomb(entityId));
	                    }
	                }
	                else { 
	                    if (showLOthers) {
	                        i(entityType+"->"+entityId+" ("+arg1+","+arg2+","+arg3+","+arg4+","+arg5+")");
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
    }


    public static void sortDistance(List<Factory> result, int srcId) {
        Collections.sort(result, (f1, f2) -> {
            return Integer.compare(dist(srcId, f1), dist(srcId, f2));
        });
	}


    
    public static void d(Object... objs) { log(LogLevel.DEBUG, objs); }
    public static void i(Object... objs) { log(LogLevel.INFO, objs); }
    public static void e(Object... objs) { log(LogLevel.ERROR, objs); }
    public static void log(LogLevel lvl, Object... objs) {
    	if (lvl.ordinal() >= LOG_LEVEL.ordinal()) {
    		System.err.print("["+lvl+"] ");
    		logAlways(objs);
    	}
    }
    public static void logAlways(Object... objs) {
    	if (objs.length == 0) {
    		return;
    	}
    	StringBuilder result = new StringBuilder();
    	for (Object obj:objs) {
    		if (obj == null) {
    			obj = "<null>";
    		}
    		result.append(obj.toString());
    	}
    	System.err.println(result.toString());
    }
    

}