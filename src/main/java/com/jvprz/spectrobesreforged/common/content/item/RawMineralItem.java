package com.jvprz.spectrobesreforged.common.content.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import java.util.List;

public final class RawMineralItem extends Item {
    private final String grade;
    public RawMineralItem(String grade) { this(grade,new Properties()); }
    public RawMineralItem(String grade,Properties properties) { super(properties.stacksTo(1)); this.grade = grade; }
    public String grade(){return grade;}
    public List<String> candidates() {
        var all=new java.util.ArrayList<String>();
        for(String family:List.of("coronium","aurorium","flashium")){
            all.add(family+"_"+grade);
            if(grade.equals("a_plus"))all.add(family+"_s");
        }
        return List.copyOf(all);
    }
    public String grade(ItemStack stack) {
        String result=RawFossilItem.data(stack).getString("Mineral");
        for(String value:List.of("a_plus","s","a","b","c"))if(result.endsWith("_"+value))return value;
        return grade;
    }
    public List<String> candidates(ItemStack stack) {
        String grade=grade(stack);
        var tag=RawFossilItem.data(stack);
        if(tag.contains("Excavation")){
            String old=tag.getString("Mineral");
            if(List.of("power_"+grade,"defense_"+grade,"health_"+grade).contains(old))return List.of(old);
        }
        String family=tag.getString("MineralFamily");
        if(!List.of("coronium","aurorium","flashium").contains(family)){
            String fixed=tag.getString("Mineral");
            family=List.of("coronium","aurorium","flashium").stream().filter(f->fixed.startsWith(f+"_")).findFirst().orElse("");
            if(family.isEmpty())return candidates();
        }
        return grade.equals("a_plus")?List.of(family+"_a_plus",family+"_s"):List.of(family+"_"+grade);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.spectrobesreforged.raw_mineral"));
        String family=RawFossilItem.data(stack).getString("MineralFamily");
        if(List.of("coronium","aurorium","flashium").contains(family))lines.add(Component.translatable("tooltip.spectrobesreforged.mineral_origin",family.substring(0,1).toUpperCase(java.util.Locale.ROOT)+family.substring(1)));
        if (RawFossilItem.data(stack).contains("Excavation")) lines.add(Component.translatable("tooltip.spectrobesreforged.excavation_saved"));
    }
}
