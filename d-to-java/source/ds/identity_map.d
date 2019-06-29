module ds.identity_map;

import dmd.root.rootobject;

struct IdentityMap(V) {
    V[void*] map;
    
    V opIndex(RootObject key) {
        return map[cast(void*)key];
    }

    void opIndexAssign(V value, RootObject key) {
        map[cast(void*)key] = value;
    }

    V* opBinaryRight(string op:"in")(RootObject key) {
        return cast(void*)key in map;
    }

    void remove(RootObject key) { 
        map.remove(cast(void*)key);
    }

    void remove(const(void)* key) { 
        map.remove(key);
    }

    auto keys(){ return map.keys; }

    auto values(){ return map.values; }

    auto dup(){ return IdentityMap(map.dup); }
}