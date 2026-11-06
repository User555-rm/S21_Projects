package com.tictactoe.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LeaderboardEntry {
private UUID userId;
private String login;
private double winRatio;
}
