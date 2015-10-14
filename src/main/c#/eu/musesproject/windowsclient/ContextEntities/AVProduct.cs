using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ContextEntities
{
    public enum AVState
    {
        expired,
        updated
    }
    public class AVProduct : IEntity
    {
        public string name { get; set; }
        public AVState state { get; set; }
    }
}
