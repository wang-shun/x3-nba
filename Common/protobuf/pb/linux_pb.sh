#!/bin/bash
~/.local/lib/proto2.4.1/bin/protoc --proto_path=../ --java_out=../../src/ \
../MainMatch.proto \
../Chat.proto \
../AllStarPb.proto \
../RankedMatchPb.proto \
../Battle.proto \
../Prop.proto \
../GameLog.proto \
../PlayerBid.proto \
../Team.proto \
../RankArenaPb.proto \
../Player.proto \
../Common.proto \
../Trade.proto \
../Scout.proto \
../TradeP2P.proto \
../TeamBeSign.proto
