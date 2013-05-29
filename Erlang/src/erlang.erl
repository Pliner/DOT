%% Copyright
-module(erlang).

dirListerLoop() ->
  receive
    list ->
      {ok, List} = file:list_dir("."),
      spammer ! {list, List};
    {file, Path} ->
      {ok, Body} = file:read_file(Path),
      spammer ! {file, Body}
  end,
  dirListerLoop().

spammerLoop() ->
  timer:sleep(1000),
  receive
    {list, List} ->
      dirLister ! {file, lists:nth(random:uniform(length(List)), List)};
    {file, Body} ->
      io:fwrite("File size is ~p~n", [byte_size(Body)]),
      dirLister ! list
  end,
  spammerLoop().


main(_) ->
  random:seed(erlang:now()),
  register(dirLister, spawn(fun() -> dirListerLoop() end)),
  register(spammer, spawn(fun() -> spammerLoop() end)),
  dirLister ! list,
  timer:sleep(10000).

