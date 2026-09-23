public enum Direction {
    UP {
        @Override
        public PositionOffset getOffset() {
            return new PositionOffset(-1, 0);
        }

        @Override
        public int getRowOffset() {
            return -1;
        }

        @Override
        public int getColOffset() {
            return 0;
        }
    },
    DOWN {
        @Override
        public PositionOffset getOffset() {
            return new PositionOffset(1, 0);
        }

        @Override
        public int getRowOffset() {
            return 1;
        }

        @Override
        public int getColOffset() {
            return 0;
        }
    },
    LEFT {
        @Override
        public PositionOffset getOffset() {
            return new PositionOffset(0, -1);
        }

        @Override
        public int getRowOffset() {
            return 0;
        }

        @Override
        public int getColOffset() {
            return -1;
        }
    },
    RIGHT {
        @Override
        public PositionOffset getOffset() {
            return new PositionOffset(0, 1);
        }

        @Override
        public int getRowOffset() {
            return 0;
        }

        @Override
        public int getColOffset() {
            return 1;
        }
    };

    public abstract PositionOffset getOffset();
    public abstract int getRowOffset();
    public abstract int getColOffset();

    public static Direction fromChar(char c) {
        switch (c) {
            case 'U': return UP;
            case 'D': return DOWN;
            case 'L': return LEFT;
            case 'R': return RIGHT;
            default: return null;
        }
    }
}
