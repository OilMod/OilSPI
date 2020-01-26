package org.oilmod.util;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ExceptionUtils {

    public static  <T extends T1, T1 extends Throwable, T2 extends Throwable> void summarizeAndClear(List<? extends T> exceptions, String message, BiFunction<String, T1, T2> toThrow) throws T2 {
        if (exceptions.isEmpty())return;
        if (exceptions.size() == 1) {
            throw toThrow.apply(message, exceptions.get(0));
        } else {
            StringBuilder sb = new StringBuilder(message);
            class ExData {
                final Throwable ex;
                int count = 1;

                ExData(Throwable ex) {
                    this.ex = ex;
                }
            }

            Map<String, ExData> map = new Object2ObjectOpenHashMap<>();
            for (Throwable e:exceptions) {
                String testStr = e.toString();
                map.compute(testStr, (s, data) -> {
                    if (data == null) {
                        return new ExData(e);
                    }
                    data.count++;
                    return data;
                });
            }
            for(ExData entry:map.values()) {
                sb.append("\nThe following exception occurred ");
                sb.append(entry.count);
                sb.append(" times: \"");
                sb.append(entry.ex.getMessage());
                sb.append('\"');
            }
            sb.append("\nWithin ");
            sb.append(exceptions.size());
            sb.append(", ");
            sb.append(map.size());
            sb.append(" unique exceptions occurred.");
            T2 ex = toThrow.apply(sb.toString(), exceptions.get(0));
            for(ExData entry:map.values()) {
                ex.addSuppressed(entry.ex);
            }
            exceptions.clear();
            throw ex;
        }
    }
}
