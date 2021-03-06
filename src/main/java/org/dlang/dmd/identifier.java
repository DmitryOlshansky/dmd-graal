package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.utils.*;

public class identifier {
    static Identifier anonymousanonymous = null;
    static int generateIdi = 0;
    private static class Key
    {
        private Loc loc = new Loc();
        private ByteSlice prefix = new ByteSlice();
        public Key(){ }
        public Key copy(){
            Key r = new Key();
            r.loc = loc.copy();
            r.prefix = prefix.copy();
            return r;
        }
        public Key(Loc loc, ByteSlice prefix) {
            this.loc = loc;
            this.prefix = prefix;
        }

        public Key opAssign(Key that) {
            this.loc = that.loc;
            this.prefix = that.prefix;
            return this;
        }
    }
    static AA<Key,Integer> generateIdWithLoccounters = new AA<Key,Integer>();

    public static class Identifier extends RootObject
    {
        public int value = 0;
        public ByteSlice name = new ByteSlice();
        // Erasure: __ctor<Ptr, int, int>
        public  Identifier(BytePtr name, int length, int value) {
            super();
            this.name = name.slice(0,length).copy();
            this.value = value;
        }

        // Erasure: __ctor<Array, int>
        public  Identifier(ByteSlice name, int value) {
            super();
            this.name = name.copy();
            this.value = value;
        }

        // Erasure: __ctor<Ptr>
        public  Identifier(BytePtr name) {
            this(name.slice(0,strlen(name)), 120);
        }

        // Erasure: anonymous<>
        public static Identifier anonymous() {
            if (identifier.anonymousanonymous != null)
            {
                return identifier.anonymousanonymous;
            }
            return identifier.anonymousanonymous = new Identifier(new ByteSlice("__anonymous"), 120);
        }

        // Erasure: create<Ptr>
        public static Identifier create(BytePtr name) {
            return new Identifier(name);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.name.getPtr(0);
        }

        // Erasure: asString<>
        public  ByteSlice asString() {
            return this.name;
        }

        // Erasure: getValue<>
        public  int getValue() {
            return this.value;
        }

        // Erasure: toHChars2<>
        public  BytePtr toHChars2() {
            BytePtr p = null;
            if ((pequals(this, Id.ctor)))
            {
                p = pcopy(new BytePtr("this"));
            }
            else if ((pequals(this, Id.dtor)))
            {
                p = pcopy(new BytePtr("~this"));
            }
            else if ((pequals(this, Id.unitTest)))
            {
                p = pcopy(new BytePtr("unittest"));
            }
            else if ((pequals(this, Id.dollar)))
            {
                p = pcopy(new BytePtr("$"));
            }
            else if ((pequals(this, Id.withSym)))
            {
                p = pcopy(new BytePtr("with"));
            }
            else if ((pequals(this, Id.result)))
            {
                p = pcopy(new BytePtr("result"));
            }
            else if ((pequals(this, Id.returnLabel)))
            {
                p = pcopy(new BytePtr("return"));
            }
            else
            {
                p = pcopy(this.toChars());
                if (((p.get() & 0xFF) == 95))
                {
                    if ((strncmp(p, new BytePtr("_staticCtor"), 11) == 0))
                    {
                        p = pcopy(new BytePtr("static this"));
                    }
                    else if ((strncmp(p, new BytePtr("_staticDtor"), 11) == 0))
                    {
                        p = pcopy(new BytePtr("static ~this"));
                    }
                    else if ((strncmp(p, new BytePtr("__invariant"), 11) == 0))
                    {
                        p = pcopy(new BytePtr("invariant"));
                    }
                }
            }
            return p;
        }

        // Erasure: dyncast<>
        public  int dyncast() {
            return DYNCAST.identifier;
        }

        public static StringTable stringtable = new StringTable();
        // Erasure: generateId<Ptr>
        public static Identifier generateId(BytePtr prefix) {
            return generateId(prefix, identifier.generateIdi += 1);
        }

        // Erasure: generateId<Ptr, int>
        public static Identifier generateId(BytePtr prefix, int i) {
            OutBuffer buf = new OutBuffer();
            try {
                buf.writestring(prefix);
                buf.print((long)i);
                return idPool(buf.peekSlice());
            }
            finally {
            }
        }

        // Erasure: generateIdWithLoc<Array, Loc>
        public static Identifier generateIdWithLoc(ByteSlice prefix, Loc loc) {
            OutBuffer idBuf = new OutBuffer();
            try {
                idBuf.writestring(prefix);
                idBuf.writestring(new ByteSlice("_L"));
                idBuf.print((long)loc.linnum);
                idBuf.writestring(new ByteSlice("_C"));
                idBuf.print((long)loc.charnum);
                Function0<Integer> __lambda7 = new Function0<Integer>() {
                    public Integer invoke() {
                     {
                        return 1;
                    }}

                };
                Function1<Integer,Integer> __lambda8 = new Function1<Integer,Integer>() {
                    public Integer invoke(Integer counter) {
                     {
                        idBuf.writestring(new ByteSlice("_"));
                        idBuf.print((long)counter);
                        return counter + 1;
                    }}

                };
                update(identifier.generateIdWithLoccounters, new Key(loc, prefix), __lambda7, __lambda8);
                return idPool(idBuf.peekSlice());
            }
            finally {
            }
        }

        // Erasure: idPool<Ptr, int>
        public static Identifier idPool(BytePtr s, int len) {
            return idPool(s.slice(0,len));
        }

        // Erasure: idPool<Array>
        public static Identifier idPool(ByteSlice s) {
            Ptr<StringValue> sv = stringtable.update(s);
            Identifier id = ((Identifier)(sv.get()).ptrvalue);
            if (id == null)
            {
                id = new Identifier((sv.get()).asString(), 120);
                (sv.get()).ptrvalue = pcopy((Object)id);
            }
            return id;
        }

        // Erasure: idPool<Ptr, int, int>
        public static Identifier idPool(BytePtr s, int len, int value) {
            return idPool(s.slice(0,len), value);
        }

        // Erasure: idPool<Array, int>
        public static Identifier idPool(ByteSlice s, int value) {
            Ptr<StringValue> sv = stringtable.insert(s, null);
            assert(sv != null);
            Identifier id = new Identifier((sv.get()).asString(), value);
            (sv.get()).ptrvalue = pcopy((Object)id);
            return id;
        }

        // Erasure: isValidIdentifier<Ptr>
        public static boolean isValidIdentifier(BytePtr str) {
            return (str != null) && isValidIdentifier(toDString(str));
        }

        // Erasure: isValidIdentifier<Array>
        public static boolean isValidIdentifier(ByteSlice str) {
            if ((str.getLength() == 0) || ((str.get(0) & 0xFF) >= 48) && ((str.get(0) & 0xFF) <= 57))
            {
                return false;
            }
            Ref<Integer> idx = ref(0);
            for (; (idx.value < str.getLength());){
                Ref<Integer> dc = ref(0x0ffff);
                BytePtr q = pcopy(utf_decodeChar(str.getPtr(0), str.getLength(), idx, dc));
                if ((q != null) || !((dc.value >= 128) && isUniAlpha(dc.value) || (isalnum(dc.value) != 0) || (dc.value == 95)))
                {
                    return false;
                }
            }
            return true;
        }

        // Erasure: lookup<Ptr, int>
        public static Identifier lookup(BytePtr s, int len) {
            return lookup(s.slice(0,len));
        }

        // Erasure: lookup<Array>
        public static Identifier lookup(ByteSlice s) {
            Ptr<StringValue> sv = stringtable.lookup(s);
            if (sv == null)
            {
                return null;
            }
            return ((Identifier)(sv.get()).ptrvalue);
        }

        // Erasure: initTable<>
        public static void initTable() {
            stringtable._init(28000);
        }


        public Identifier() {}

        public Identifier copy() {
            Identifier that = new Identifier();
            that.value = this.value;
            that.name = this.name;
            return that;
        }
    }
}
