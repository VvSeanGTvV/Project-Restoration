let flowgate = (block) => {
    let h = () => extend(OverflowGate.OverflowGateBuild, block, {
        let lastItem;
        let lastInput;
        let time;

        public acceptItem(source, item) {
            //Building target = getTargetTile(lastitem, this, source, false);

            return source.team == team && this.items.total() == 0 && lastItem == null;
        }

        public handleItem(Building source, Item item){
            this.items.add(item, 1);
            this.lastItem = item;
            this.time = 0;
            this.lastInput = source;
        }

        public update() {
            if(this.lastItem == null && this.items.total() > 0){
                this.items.clear();
            }

            if(this.lastItem != null){
                let target = this.getTargetTile(this.lastItem, this.lastInput, false);

                if(target != null){
                    this.getTargetTile(this.lastItem, this.lastInput, true);
                    target.handleItem(this, this.lastItem);
                    this.items.remove(this.lastItem, 1);
                    this.lastItem = null;
                }
            }
        }

        getTileTarget(item, src, flip){
            let from = this.relativeToEdge(source.tile);
            if (from == -1) return null;
            let to = this.nearby(Mathf.mod(from + 2, 4));
            let
                canForward = to != null && to.acceptItem(this, item) && to.team == team && !(to.block instanceof OverflowGateRevampBuild),
                inv = this.block.invert == enabled;

            if(!canForward || inv){
                let a = this.nearby(Mathf.mod(from - 1, 4));
                let b = this.nearby(Mathf.mod(from + 1, 4));
                let ac = a != null && a.team == team && a.acceptItem(this, item);
                let bc = b != null && b.team == team && b.acceptItem(this, item);

                if(!ac && !bc){
                    return inv && canForward ? to : null;
                }

                if(ac && !bc){
                    to = a;
                    }else if(bc && !ac){
                        to = b;
                            }else{
                                if(this.rotation == 0){
                                    to = a;
                                    if(flip) this.rotation = 1;
                                } else {
                                    to = b;
                                    if(flip) this.rotation = 0;
                                }
                            }
            }

            return to;
        }
    });
    return h;
};

Blocks.overflowGate.buildType = flowgate(Blocks.overflowGate);
Blocks.underflowGate.buildType = flowgate(Blocks.underflowGate);