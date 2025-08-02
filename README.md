# Throwable

This mod can make everything throwable, with a configuration system.

If you want to make something throwable, add to `#throwable:throwable` item tag.

By default, all swords can be thrown. Once you have thrown, you need to click it to pick up.

## Configuration

### Global

Create file `.minecraft/config/throwable.json` (may exist).

```json5
{
  //Whether only owner can pick up the throwable thing.
  "ownerPickUpOnly": false
}
```

### Item Specific

Create file `data/<item namespace>/throwable/throwable/<item id>.json`, then write following contents:

```json5
{
  //All keys are optional, following are default values
  //Use time to grant critical damage
  "maxUseTime": 72000,
  //Damage Ratio to item base damage
  "damageScale": 0.5,
  //Render Size Scale
  "scale": 1,
  //Rotate based on renders in item frame
  "rotateOffset": 0,
  //Sound played when item thrown
  "throwSound": "minecraft:item.trident.throw",
  //Sound played when hit ground
  "hitGroundSound": "minecraft:item.trident.hit_ground"
}
```